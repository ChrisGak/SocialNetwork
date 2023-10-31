package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.model.dto.ChangeCounterRequest
import me.kristinasaigak.otus.model.dto.GetCounterRequest
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
            request.bodyToMono(ChangeCounterRequest::class.java)
                    .flatMap { body ->
                        logger.debug("Received increment counter request $request")
                        counterService.incrementCounter(body)
                                .then(
                                        ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .build()
                                )
                    }
                    .metric(MetricTitle.INCREMENT_COUNTER_HANDLED.value)

    fun decrementCounter(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(ChangeCounterRequest::class.java)
                    .flatMap { body ->
                        logger.debug("Received decrement counter request $request")
                        counterService.decrementCounter(body)
                                .then(
                                        ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .build()
                                )
                    }
                    .metric(MetricTitle.DECREMENT_COUNTER_HANDLED.value)

    fun getCounter(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(GetCounterRequest::class.java)
                    .flatMap { body ->
                        logger.debug("Received request $request")
                        counterService.getCounter(body)
                                .flatMap {
                                    ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(it)
                                }
                    }
                    .metric(MetricTitle.GET_COUNTER_HANDLED.value)
}