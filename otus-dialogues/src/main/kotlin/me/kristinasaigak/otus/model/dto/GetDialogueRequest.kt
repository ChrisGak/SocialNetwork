package me.kristinasaigak.otus.model.dto

data class GetDialogueRequest(
        var firstUserId: String,
        var secondUserId: String)