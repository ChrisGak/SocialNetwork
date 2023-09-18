package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.DialogueMessageDto
import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.repository.tarantool.TarantoolDialogueRepository
import me.kristinasaigak.otus.utils.getCurrentUserId
import me.kristinasaigak.otus.utils.toIntOrThrow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TarantoolDialogueServiceImpl(
        private val dialogueRepository: TarantoolDialogueRepository,
) : DialogueService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun createDialogue(dialogueMessage: DialogueMessageRequest,
                                toUserId: String): Mono<Void> =
            getCurrentUserId()
                .map { currentUserId ->
                    logger.debug("Send message request body: $dialogueMessage, from user $currentUserId to user: $toUserId")
                    dialogueRepository.saveMessage(
                            fromUserId = currentUserId,
                            toUserId = toUserId,
                            text = dialogueMessage.text,
                    )
                }
                .then()

    override fun getDialogue(toUserId: Int): Mono<List<DialogueMessageDto>> {
        return getCurrentUserId()
                .flatMap { currentUserId ->
                    logger.debug("Get dialog messages between users $currentUserId and $toUserId")
                    Mono.fromCallable {
                        dialogueRepository.findAllDialogueMessagesByUsers(currentUserId, toUserId.toString())
                                .map {
                                    DialogueMessageDto(
                                            fromUserId = it.fromUserId.toIntOrThrow(),
                                            toUserId = it.toUserId.toIntOrThrow(),
                                            text = it.text
                                    )
                                }
                    }
                }
    }
}