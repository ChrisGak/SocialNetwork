package me.kristinasaigak.otus.model.dto

data class GetCounterRequest(
        var firstUserId: String,
        var secondUserId: String)