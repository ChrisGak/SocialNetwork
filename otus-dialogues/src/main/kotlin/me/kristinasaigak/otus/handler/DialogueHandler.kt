package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.model.dto.GetDialogueRequest
import me.kristinasaigak.otus.model.dto.GetDialogueResponse
import me.kristinasaigak.otus.service.TarantoolDialogueServiceImpl
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

@Component
class DialogueHandler(
        private val dialogueServiceImpl: TarantoolDialogueServiceImpl
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendMessage(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(DialogueMessageRequest::class.java)
                    .flatMap { message ->
                        val processId = request.headers().firstHeader(PROCESS_ID_HEADER_NAME)
                        logger.debug("Received request $request, processId: $processId")
                        dialogueServiceImpl.createDialogue(message)
                        ServerResponse.status(HttpStatus.CREATED).build()
                    }
                    .metric(MetricTitle.SEND_MESSAGE_HANDLED.value)

    fun getDialogue(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(GetDialogueRequest::class.java)
                    .flatMap { body ->
                        val processId = request.headers().firstHeader(PROCESS_ID_HEADER_NAME)
                        logger.debug("Received request $request, processId: $processId")
                        dialogueServiceImpl.getDialogue(body)
                                .let { dialogueMessages ->
                                    ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(
                                                    GetDialogueResponse(
                                                            messages = dialogueMessages
                                                    )
                                            )
                                }
                                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build())
                    }
                    .also {
                        logger.debug("Received request $request")
                    }
                    .metric(MetricTitle.GET_DIALOGUE_HANDLE.value)
}