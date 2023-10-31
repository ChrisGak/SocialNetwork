package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.model.dto.GetDialogueRequest
import me.kristinasaigak.otus.model.dto.GetDialogueResponse
import me.kristinasaigak.otus.service.CounterService
import me.kristinasaigak.otus.service.impl.TarantoolDialogueServiceImpl
import me.kristinasaigak.otus.utils.MetricTitle
import me.kristinasaigak.otus.utils.PROCESS_ID_HEADER_NAME
import me.kristinasaigak.otus.utils.metric
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DialogueHandler(
        private val dialogueServiceImpl: TarantoolDialogueServiceImpl,
        private val counterService: CounterService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendMessage(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(DialogueMessageRequest::class.java)
                    .flatMap { message ->
                        val processId = request.headers().firstHeader(PROCESS_ID_HEADER_NAME)
                        logger.debug("Received request $request, processId: $processId")

                        logger.debug("Compensable transaction - increment counter")
                        counterService.incrementCounter(toUserId = message.toUserId, fromUserId = message.fromUserId)
                                .flatMap {
                                    Mono.fromCallable { dialogueServiceImpl.createDialogue(message) }
                                            .flatMap {
                                                ServerResponse.status(HttpStatus.CREATED).build()
                                            }
                                }
                                .onErrorResume {
                                    logger.error("Error occurred: ${it.message}")
                                    logger.debug("Compensating transaction - decrement counter")
                                    counterService.decrementCounter(toUserId = message.toUserId, fromUserId = message.fromUserId)
                                            .flatMap {
                                                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
                                            }
                                }
                    }
                    .metric(MetricTitle.SEND_MESSAGE_HANDLED.value)

    fun getDialogue(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(GetDialogueRequest::class.java)
                    .flatMap { body ->
                        val processId = request.headers().firstHeader(PROCESS_ID_HEADER_NAME)
                        logger.debug("Received request $request, processId: $processId")
                        dialogueServiceImpl.getDialogue(body).toMono()
                                .flatMap { dialogueMessages ->
                                    logger.debug("Compensable transaction - decrement counter")
                                    counterService.decrementCounter(body.firstUserId, body.secondUserId, size = dialogueMessages.size)
                                            .then(
                                                    ServerResponse.ok()
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .bodyValue(
                                                                    GetDialogueResponse(
                                                                            messages = dialogueMessages
                                                                    )
                                                            )
                                            )
                                }
                    }
                    .onErrorResume {
                        logger.error("Error occurred: ${it.message}")
                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
                    }
                    .metric(MetricTitle.GET_DIALOGUE_HANDLE.value)
}