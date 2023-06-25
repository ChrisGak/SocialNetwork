package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.model.entity.DialogueMessage
import me.kristinasaigak.otus.repository.DialogueRepository
import me.kristinasaigak.otus.utils.getCurrentUserId
import me.kristinasaigak.otus.utils.toIntOrThrow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DialogueService(
        private val dialogueRepository: DialogueRepository,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createDialogue(dialogueMessage: DialogueMessageRequest,
                       toUserId: String): Mono<DialogueMessage> {
        return getCurrentUserId()
                .flatMap { currentUserId ->
                    logger.debug("Send message request body: $dialogueMessage, from user $currentUserId to user: $toUserId")
                    dialogueRepository.save(DialogueMessage(
                            text = dialogueMessage.text,
                            fromUserId = currentUserId.toIntOrThrow(),
                            toUserId = toUserId.toIntOrThrow()))
                }
    }

    fun getDialogue(toUserId: Int): Mono<List<DialogueMessage>> {
        return getCurrentUserId()
                .flatMap {
                    it.toIntOrThrow().let { currentUserId ->
                        logger.debug("Get dialog messages between users $currentUserId and $toUserId")
                        dialogueRepository.findAllDialogueMessagesByUsers(currentUserId, toUserId)
                                .collectList().flatMap { dialogueMessages ->
                                    dialogueMessages.toMono()
                                }
                    }
                }
    }
}