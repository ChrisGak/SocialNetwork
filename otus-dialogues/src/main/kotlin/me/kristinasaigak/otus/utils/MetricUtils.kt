package me.kristinasaigak.otus.utils

import reactor.core.publisher.Mono

enum class MetricTitle(val value: String) {
    SEND_MESSAGE_HANDLED("otus.dialogue.send-message.handled"),
    GET_DIALOGUE_HANDLE("otus.dialogue.get-messages.handled")
}

fun <T> Mono<T>.metric(metricName: String): Mono<T> =
        this.name(metricName)
                .metrics()