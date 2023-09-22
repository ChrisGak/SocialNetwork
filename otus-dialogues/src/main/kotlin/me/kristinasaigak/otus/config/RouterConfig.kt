package me.kristinasaigak.otus.config

import me.kristinasaigak.otus.handler.DialogueHandler
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
        private val dialogueHandler: DialogueHandler,
) {

    @Bean
    fun routes(): RouterFunction<ServerResponse> {
        return route(
                POST("/internal/dialogue/send").and(accept(MediaType.APPLICATION_JSON)), dialogueHandler::sendMessage)
                .andRoute(POST("/internal/dialogue/list").and(accept(MediaType.APPLICATION_JSON)), dialogueHandler::getDialogue)
    }
}