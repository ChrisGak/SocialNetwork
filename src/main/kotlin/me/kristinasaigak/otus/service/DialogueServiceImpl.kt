package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.DialogueMessageDto
import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.model.entity.DialogueMessage
import me.kristinasaigak.otus.repository.DialogueRepository
import me.kristinasaigak.otus.utils.getCurrentUserId
import me.kristinasaigak.otus.utils.toIntOrThrow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Deprecated("Use TarantoolDialogueServiceImpl instead.")
@Service
class DialogueServiceImpl(
        private val dialogueRepository: DialogueRepository,
) : DialogueService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun createDialogue(dialogueMessage: DialogueMessageRequest,
                                toUserId: String): Mono<Void> =
            getCurrentUserId()
                    .flatMap { currentUserId ->
                        logger.debug("Send message request body: $dialogueMessage, from user $currentUserId to user: $toUserId")
                        dialogueRepository.save(DialogueMessage(
                                text = dialogueMessage.text,
                                fromUserId = currentUserId.toIntOrThrow(),
                                toUserId = toUserId.toIntOrThrow()))
                                .then()
                    }

    override fun getDialogue(toUserId: Int): Mono<List<DialogueMessageDto>> {
        return getCurrentUserId()
                .flatMap {
                    it.toIntOrThrow().let { currentUserId ->
                        logger.debug("Get dialog messages between users $currentUserId and $toUserId")
                        dialogueRepository.findAllDialogueMessagesByUsers(currentUserId, toUserId)
                                .map {
                                    DialogueMessageDto(
                                            fromUserId = it.fromUserId,
                                            toUserId = it.toUserId,
                                            text = it.text
                                    )
                                }
                                .collectList()
                    }
                }
    }
}