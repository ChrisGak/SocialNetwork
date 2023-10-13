package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.DialogueMessageRequest
import me.kristinasaigak.otus.model.dto.GetDialogueRequest
import org.springframework.stereotype.Service

@Service
class CounterService {

    // private val valueOperations: ValueOperations<UnreadMessagesCountDto, String>

    fun incrementCounter(dialogueMessage: DialogueMessageRequest) {
    }

    fun decrementCounter(body: GetDialogueRequest) {
    }
}