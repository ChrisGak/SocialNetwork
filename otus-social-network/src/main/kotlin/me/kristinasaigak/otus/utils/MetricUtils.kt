package me.kristinasaigak.otus.utils

import reactor.core.publisher.Mono

enum class MetricTitle(val value: String) {
    REQUEST_HANDLE("otus.request.handled"),
    WEBSOCKET_REQUEST_HANDLE("otus.websocket.request.handled")
}
enum class MetricTag(val value: String) {
    URL("url")
}

fun <T> Mono<T>.metric(metricName: String, tagName: String, tagValue: String): Mono<T> =
        this.name(metricName)
                .tag(tagName, tagValue)
                .metrics()
fun <T> Mono<T>.metricHandledRequest(tagValue: String): Mono<T> =
        this.name(MetricTitle.REQUEST_HANDLE.value)
                .tag(MetricTag.URL.value, tagValue)
                .metrics()