package me.kristinasaigak.otus.service

import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono

interface CounterService {

    fun incrementCounter(toUserId: String, fromUserId: String, size: Int = 1): Mono<ClientResponse>

    fun decrementCounter(toUserId: String, fromUserId: String, size: Int = 1): Mono<ClientResponse>
}