package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.model.dto.GetDialogueRequest
import me.kristinasaigak.otus.service.CounterService
import me.kristinasaigak.otus.utils.MetricTitle
import me.kristinasaigak.otus.utils.metric
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class CounterHandler(private val counterService: CounterService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun incrementCounter(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(GetDialogueRequest::class.java)
                    .flatMap { body ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .build()
                    }
                    .also {
                        logger.debug("Received request $request")
                    }
                    .metric(MetricTitle.INCREMENT_COUNTER_HANDLED.value)

    fun decrementCounter(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(GetDialogueRequest::class.java)
                    .flatMap { body ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .build()
                    }
                    .also {
                        logger.debug("Received request $request")
                    }
                    .metric(MetricTitle.DECREMENT_COUNTER_HANDLED.value)
}