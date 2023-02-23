package me.kristinasaigak.otus.model.api

data class UserDto(
    var id: String?,
    var first_name: String,
    var second_name: String,
    var age: Int,
    var biography: String? = null,
    var city: String
)
