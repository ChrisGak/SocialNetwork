package me.kristinasaigak.otus.model.dto

data class UserDto(
    var id: Int?,
    var first_name: String,
    var second_name: String,
    var age: Int? = null,
    var biography: String? = null,
    var city: String? = null,
)
