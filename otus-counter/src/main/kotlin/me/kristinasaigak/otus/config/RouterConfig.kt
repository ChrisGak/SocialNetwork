package me.kristinasaigak.otus.config

import me.kristinasaigak.otus.handler.CounterHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class RouterConfig(
        private val counterHandler: CounterHandler
) {
    @Bean
    fun routes(): RouterFunction<ServerResponse> {
        return route(
                POST("/internal/dialogue/counter/increment").and(accept(MediaType.APPLICATION_JSON)), counterHandler::incrementCounter)
                .andRoute(POST("/internal/dialogue/counter/decrement").and(accept(MediaType.APPLICATION_JSON)), counterHandler::decrementCounter)
    }
}