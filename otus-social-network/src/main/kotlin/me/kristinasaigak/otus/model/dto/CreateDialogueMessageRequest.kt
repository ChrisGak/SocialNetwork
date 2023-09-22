package me.kristinasaigak.otus.model.dto

data class CreateDialogueMessageRequest(
        var fromUserId: String,
        var toUserId: String,
        var text: String)
