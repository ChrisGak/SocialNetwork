package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.service.TarantoolDialogueServiceImpl
import me.kristinasaigak.otus.utils.RequestParams
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
        private val dialogueServiceImpl: TarantoolDialogueServiceImpl
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendMessage(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(DialogueMessageRequest::class.java)
                    .flatMap { message ->
                        val toUserId = request.pathVariable(RequestParams.USER_ID.value)
                        dialogueServiceImpl.createDialogue(message, toUserId)
                                .then(
                                        ServerResponse.status(HttpStatus.CREATED).build()
                                )
                    }.also {
                        logger.debug("Received request $request")
                    }

    fun getDialogue(request: ServerRequest): Mono<ServerResponse> =
            request.pathVariable(RequestParams.USER_ID.value).let {
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
                    .also {
                        logger.debug("Received request $request")
                    }
}