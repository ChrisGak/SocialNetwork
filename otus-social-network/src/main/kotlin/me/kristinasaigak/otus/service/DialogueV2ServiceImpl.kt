package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.CreateDialogueMessageRequest
import me.kristinasaigak.otus.model.dto.DialogueMessageDto
import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.model.dto.GetDialogueRequest
import me.kristinasaigak.otus.model.dto.GetDialogueResponse
import me.kristinasaigak.otus.utils.PROCESS_ID_HEADER_NAME
import me.kristinasaigak.otus.utils.generateProcessId
import me.kristinasaigak.otus.utils.getCurrentUserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class DialogueV2ServiceImpl(
        private val dialoguesClient: WebClient
) : DialogueService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun createDialogue(dialogueMessage: DialogueMessageRequest,
                                toUserId: String): Mono<Void> =
            getCurrentUserId()
                    .flatMap { currentUserId ->
                        val processId = generateProcessId()
                        logger.debug("Send message request body: $dialogueMessage, from user $currentUserId to user: $toUserId. ProcessId $processId")
                        dialoguesClient.post()
                                .uri("/internal/dialogue/send")
                                .header(PROCESS_ID_HEADER_NAME, processId)
                                .bodyValue(
                                        CreateDialogueMessageRequest(
                                                fromUserId = currentUserId,
                                                toUserId = toUserId,
                                                text = dialogueMessage.text
                                        )
                                )
                                .exchangeToMono { clientResponse ->
                                    logger.debug("Message sent. StatusCode: ${clientResponse.statusCode()}")
                                    clientResponse.toMono()
                                }
                                .then()
                    }

    override fun getDialogue(toUserId: Int): Mono<List<DialogueMessageDto>> {
        return getCurrentUserId()
                .flatMap { currentUserId ->
                    val processId = generateProcessId()
                    logger.debug("Get dialog messages between users $currentUserId and $toUserId. ProcessId $processId")
                    dialoguesClient.post()
                            .uri("/internal/dialogue/list")
                            .header(PROCESS_ID_HEADER_NAME, processId)
                            .bodyValue(
                                    GetDialogueRequest(
                                            firstUserId = currentUserId,
                                            secondUserId = toUserId.toString()
                                    )
                            )
                            .exchangeToMono { clientResponse ->
                                clientResponse.bodyToMono(GetDialogueResponse::class.java)
                                        .map {
                                            it.messages
                                        }
                            }
                }
    }
}