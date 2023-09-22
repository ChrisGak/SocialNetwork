package me.kristinasaigak.otus.exception

import java.lang.Exception

class IdNotParsedException(private val description: String): Exception(description)