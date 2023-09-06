package me.kristinasaigak.otus.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PostDto(
        @JsonProperty("postId")
        var postId: String,
        @JsonProperty("postText")
        var postText: String,
        @JsonProperty("author_user_id")
        var author_user_id: String
)