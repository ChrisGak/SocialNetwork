package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.DialogueMessageDto
import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.model.dto.GetDialogueRequest
import me.kristinasaigak.otus.repository.tarantool.TarantoolDialogueRepository
import me.kristinasaigak.otus.utils.toIntOrThrow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TarantoolDialogueServiceImpl(
        private val dialogueRepository: TarantoolDialogueRepository,
) : DialogueService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun createDialogue(dialogueMessage: DialogueMessageRequest) {
        logger.debug("Send message request body: $dialogueMessage, from user ${dialogueMessage.fromUserId} to user: ${dialogueMessage.toUserId}")
        return dialogueRepository.saveMessage(
                fromUserId = dialogueMessage.fromUserId,
                toUserId = dialogueMessage.toUserId,
                text = dialogueMessage.text,
        )
    }

    override fun getDialogue(body: GetDialogueRequest): List<DialogueMessageDto> {
        logger.debug("Get dialog messages between users ${body.firstUserId} and ${body.secondUserId}")
        return dialogueRepository.findAllDialogueMessagesByUsers(body.firstUserId, body.secondUserId)
                .map {
                    DialogueMessageDto(
                            fromUserId = it.fromUserId.toIntOrThrow(),
                            toUserId = it.toUserId.toIntOrThrow(),
                            text = it.text
                    )
                }
    }
}