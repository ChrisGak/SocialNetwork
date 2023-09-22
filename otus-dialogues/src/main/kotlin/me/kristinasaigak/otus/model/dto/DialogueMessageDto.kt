package me.kristinasaigak.otus.model.dto

data class DialogueMessageDto(var fromUserId: Int,
                              var toUserId: Int,
                              var text: String)
