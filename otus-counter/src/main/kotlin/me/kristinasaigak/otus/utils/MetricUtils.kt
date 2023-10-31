package me.kristinasaigak.otus.utils

import reactor.core.publisher.Mono

enum class MetricTitle(val value: String) {
    INCREMENT_COUNTER_HANDLED("otus.counter.increment.handled"),
    DECREMENT_COUNTER_HANDLED("otus.counter.decrement.handled"),
    GET_COUNTER_HANDLED("otus.counter.get.handled")
}

fun <T> Mono<T>.metric(metricName: String): Mono<T> =
        this.name(metricName)
                .metrics()