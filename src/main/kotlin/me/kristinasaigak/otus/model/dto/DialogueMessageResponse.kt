package me.kristinasaigak.otus.model.dto

data class DialogueMessageResponse(var fromUserId: Int,
                                   var toUserId: Int,
                                   var text: String)
