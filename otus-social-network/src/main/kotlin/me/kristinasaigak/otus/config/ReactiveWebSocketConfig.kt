package me.kristinasaigak.otus.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
@EnableWebFlux
class ReactiveWebSocketConfig {
    @Bean
    fun websocketHandlerAdapter() = WebSocketHandlerAdapter()

    @Bean
    fun handlerMapping(wsh: WebSocketHandler?): SimpleUrlHandlerMapping? {
        return SimpleUrlHandlerMapping(mapOf("/post/feed/posted" to wsh), 1)
    }
}