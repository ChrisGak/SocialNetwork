package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.service.DialogueV2ServiceImpl
import me.kristinasaigak.otus.utils.RequestParams
import me.kristinasaigak.otus.utils.generateProcessId
import me.kristinasaigak.otus.utils.metricHandledRequest
import me.kristinasaigak.otus.utils.toIntOrThrow
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class DialogueHandler(
        private val dialogueServiceImpl: DialogueV2ServiceImpl
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Deprecated("Use me.kristinasaigak.otus.handler.DialogueHandler#sendMessageV2 instead")
    fun sendMessage(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(DialogueMessageRequest::class.java)
                    .flatMap { message ->
                        logger.warn("Received request for deprecated API. Use '/v2/dialog/{userId}/send' instead")
                        val processId = generateProcessId()
                        logger.debug("Received request $request, processId $processId")
                        val toUserId = request.pathVariable(RequestParams.USER_ID.value)
                        dialogueServiceImpl.createDialogue(message, toUserId)
                                .then(
                                        ServerResponse.status(HttpStatus.CREATED).build()
                                )
                    }
                    .metricHandledRequest("/dialog/{userId}/send")

    fun sendMessageV2(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(DialogueMessageRequest::class.java)
                    .flatMap { message ->
                        val processId = generateProcessId()
                        logger.debug("Received request $request, processId $processId")
                        val toUserId = request.pathVariable(RequestParams.USER_ID.value)
                        dialogueServiceImpl.createDialogue(message, toUserId)
                                .then(
                                        ServerResponse.status(HttpStatus.CREATED).build()
                                )
                    }
                    .onErrorResume {
                        logger.error("Error received from Dialogues module: ${it.message}")
                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
                    }
                    .metricHandledRequest("/v2/dialogue/{userId}/send")

    @Deprecated("Use me.kristinasaigak.otus.handler.DialogueHandler#getDialogueV2 instead")
    fun getDialogue(request: ServerRequest): Mono<ServerResponse> =
            request.pathVariable(RequestParams.USER_ID.value).let {
                val processId = generateProcessId()
                logger.warn("Received request for deprecated API. Use '/v2/dialog/{userId}/list' instead")
                logger.debug("Received request $request, processId $processId")
                dialogueServiceImpl.getDialogue(it.toIntOrThrow())
                        .flatMap { dialogueMessages ->
                            ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(
                                            dialogueMessages
                                    )
                        }
                        .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build())
            }
                    .metricHandledRequest("/dialog/{userId}/list")

    fun getDialogueV2(request: ServerRequest): Mono<ServerResponse> =
            request.pathVariable(RequestParams.USER_ID.value).let {
                dialogueServiceImpl.getDialogue(it.toIntOrThrow())
                        .flatMap { dialogueMessages ->
                            ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(
                                            dialogueMessages
                                    )
                        }
                        .onErrorResume {
                            logger.error("Error received from Dialogues module: ${it.message}")
                            ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
                        }
                        .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build())
            }
                    .metricHandledRequest("/v2/dialogue/{userId}/list")
}