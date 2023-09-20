package me.kristinasaigak.otus.utils

import me.kristinasaigak.otus.exception.IdNotParsedException
import java.lang.Exception
import java.security.MessageDigest

const val PROCESS_ID_HEADER_NAME = "PROCESS_ID"

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