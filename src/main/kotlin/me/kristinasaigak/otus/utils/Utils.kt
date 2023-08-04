package me.kristinasaigak.otus.utils

import me.kristinasaigak.otus.exception.IdNotParsedException
import java.lang.Exception
import java.security.MessageDigest

enum class RequestParams(
        val value: String
) {
    ID("id"),
    USER_ID("userId"),
    FIRST_NAME("first_name"),
    SECOND_NAME("second_name"),
    OFFSET("offset"),
    LIMIT("limit"),
}

fun hash(password: String): String {
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun String.toIntOrThrow(): Int {
    return try {
        this.toInt()
    } catch (er: Exception) {
        throw IdNotParsedException("The string value failed to parse to Int")
    }
}