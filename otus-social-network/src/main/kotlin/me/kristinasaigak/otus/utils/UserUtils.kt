package me.kristinasaigak.otus.utils

import org.springframework.security.core.context.ReactiveSecurityContextHolder
import reactor.core.publisher.Mono

fun getCurrentUserId(): Mono<String> =
        ReactiveSecurityContextHolder.getContext()
                .map {
                    println("Security context: $it")
                    it.authentication.principal.toString()
                }