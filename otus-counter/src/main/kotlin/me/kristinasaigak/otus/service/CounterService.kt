package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.ChangeCounterRequest
import me.kristinasaigak.otus.model.dto.GetCounterRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Service
class CounterService {

    // private val valueOperations: ValueOperations<UnreadMessagesCountDto, String>
    private val counter: ConcurrentHashMap<String, Int> = ConcurrentHashMap()
    private val logger = LoggerFactory.getLogger(javaClass)

    fun incrementCounter(body: ChangeCounterRequest): Mono<Int> = Mono.fromCallable {
        logger.info("Increment counter: $body")
        counter.compute(body.getKey()) { _, value -> (value ?: 0).plus(body.size) }
    }

    fun decrementCounter(body: ChangeCounterRequest): Mono<Int> = Mono.fromCallable {
        logger.info("Decrement counter: $body")
        counter.compute(body.getKey()) { _, value -> (value ?: 0).minus(body.size) }
    }

    fun getCounter(body: GetCounterRequest): Mono<Int> = Mono.fromCallable {
        val counterValue = counter["${body.firstUserId}:${body.secondUserId}"] ?: 0
        logger.info("Get counter for $body: $counterValue")
        counterValue
    }

    private fun ChangeCounterRequest.getKey() = "${firstUserId}:${secondUserId}"
}