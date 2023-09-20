package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.DialogueMessageDto
import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import reactor.core.publisher.Mono

interface DialogueService {

    fun createDialogue(dialogueMessage: DialogueMessageRequest,
                       toUserId: String): Mono<Void>

    fun getDialogue(toUserId: Int): Mono<List<DialogueMessageDto>>
}