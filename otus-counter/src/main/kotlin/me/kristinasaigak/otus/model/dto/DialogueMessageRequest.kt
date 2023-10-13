package me.kristinasaigak.otus.model.dto

data class DialogueMessageRequest(
        var fromUserId: String,
        var toUserId: String,
        var text: String)