package me.kristinasaigak.otus.model.dto

data class ChangeCounterRequest(
        var firstUserId: String,
        var secondUserId: String,
        var size: Int)