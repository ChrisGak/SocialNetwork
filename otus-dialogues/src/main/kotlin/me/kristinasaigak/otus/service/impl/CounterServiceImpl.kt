package me.kristinasaigak.otus.service.impl

import me.kristinasaigak.otus.model.dto.ChangeCounterRequest
import me.kristinasaigak.otus.service.CounterService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class CounterServiceImpl(
        private val counterServiceClient: WebClient
) : CounterService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun incrementCounter(toUserId: String, fromUserId: String, size: Int): Mono<ClientResponse> {
        logger.debug("Send increment unread messages: from user $fromUserId to user: $toUserId")
        return counterServiceClient.post()
                .uri("/internal/dialogue/counter/increment")
                .bodyValue(
                        ChangeCounterRequest(
                                firstUserId = fromUserId,
                                secondUserId = toUserId,
                                size = size
                        )
                )
                .exchangeToMono { clientResponse ->
                    logger.debug("Message sent. StatusCode: ${clientResponse.statusCode()}")
                    clientResponse.toMono()
                }
    }

    override fun decrementCounter(toUserId: String, fromUserId: String, size: Int): Mono<ClientResponse> {
        logger.debug("Send decrement unread messages: from user $fromUserId and $toUserId")
        return counterServiceClient.post()
                .uri("/internal/dialogue/counter/decrement")
                .bodyValue(
                        ChangeCounterRequest(
                                firstUserId = fromUserId,
                                secondUserId = toUserId,
                                size = size
                        )
                )
                .exchangeToMono { clientResponse ->
                    logger.debug("Message sent. StatusCode: ${clientResponse.statusCode()}")
                    clientResponse.toMono()
                }
    }
}