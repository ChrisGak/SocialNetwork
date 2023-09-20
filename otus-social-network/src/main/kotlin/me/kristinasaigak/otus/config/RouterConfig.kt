package me.kristinasaigak.otus.config

import me.kristinasaigak.otus.handler.DialogueHandler
import me.kristinasaigak.otus.handler.FriendHandler
import me.kristinasaigak.otus.handler.PostHandler
import me.kristinasaigak.otus.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.PUT
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class RouterConfig(
        private val userHandler: UserHandler,
        private val postHandler: PostHandler,
        private val friendHandler: FriendHandler,
        private val dialogueHandler: DialogueHandler,
) {

    @Bean
    fun routes(): RouterFunction<ServerResponse> {
        return route(GET("/user/search").and(accept(MediaType.APPLICATION_JSON)), userHandler::search)
                .andRoute(GET("/user/get/{userId}").and(accept(MediaType.APPLICATION_JSON)), userHandler::getUserById)
                .andRoute(PUT("/friend/add/{userId}").and(accept(MediaType.APPLICATION_JSON)), friendHandler::addFriend)
                .andRoute(PUT("/friend/delete/{userId}").and(accept(MediaType.APPLICATION_JSON)), friendHandler::deleteFriend)
                .andRoute(POST("/user/register").and(accept(MediaType.APPLICATION_JSON)), userHandler::register)
                .andRoute(POST("/post/create").and(accept(MediaType.APPLICATION_JSON)), postHandler::createPost)
                .andRoute(GET("/post/get").and(accept(MediaType.APPLICATION_JSON)), postHandler::getPost)
                .andRoute(GET("/post/feed").and(accept(MediaType.APPLICATION_JSON)), postHandler::getPosts)
                .andRoute(PUT("/post/delete").and(accept(MediaType.APPLICATION_JSON)), postHandler::removePost)
                .andRoute(POST("/dialog/{userId}/send").and(accept(MediaType.APPLICATION_JSON)), dialogueHandler::sendMessage)
                .andRoute(GET("/dialog/{userId}/list").and(accept(MediaType.APPLICATION_JSON)), dialogueHandler::getDialogue)
                .andRoute(POST("/v2/dialogue/{userId}/send").and(accept(MediaType.APPLICATION_JSON)), dialogueHandler::sendMessageV2)
                .andRoute(GET("/v2/dialogue/{userId}/list").and(accept(MediaType.APPLICATION_JSON)), dialogueHandler::getDialogueV2)
    }

    @Bean
    fun authRoute(): RouterFunction<ServerResponse?> {
        return route(POST("/login").and(accept(MediaType.APPLICATION_JSON)), userHandler::login)
    }
}