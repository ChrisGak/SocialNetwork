package me.kristinasaigak.otus.utils

import org.springframework.security.core.context.ReactiveSecurityContextHolder
import reactor.core.publisher.Mono

fun getCurrentUserId(): Mono<String> =
        ReactiveSecurityContextHolder.getContext()
                .map {
                    it.authentication.principal.toString()
                }