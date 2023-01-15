package me.kristinasaigak.otus.config

import me.kristinasaigak.otus.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class RouterConfig(
    private val handler: UserHandler
) {

    @Bean
    fun routes(): RouterFunction<ServerResponse?>? {
        return route(GET("/user/search").and(accept(MediaType.APPLICATION_JSON)), handler::search)
            .andRoute(GET("/user/get/{userId}").and(accept(MediaType.APPLICATION_JSON)), handler::getUserById)
            .andRoute(POST("/login").and(accept(MediaType.APPLICATION_JSON)), handler::login)
            .andRoute(POST("/user/register").and(accept(MediaType.APPLICATION_JSON)), handler::register)
    }
}