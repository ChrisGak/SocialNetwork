package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.DialogueMessageDto
import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.model.dto.GetDialogueRequest

interface DialogueService {

    fun createDialogue(dialogueMessage: DialogueMessageRequest)

    fun getDialogue(body: GetDialogueRequest): List<DialogueMessageDto>
}