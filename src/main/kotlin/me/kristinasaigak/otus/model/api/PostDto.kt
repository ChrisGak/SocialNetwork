package me.kristinasaigak.otus.model.api

data class PostDto(
        var text: String,
        var author: String? = null
)