package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.service.UserService
import me.kristinasaigak.otus.utils.RequestParams
import me.kristinasaigak.otus.utils.metricHandledRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class FriendHandler(private val userService: UserService) {

    fun addFriend(request: ServerRequest): Mono<ServerResponse> =
            request.pathVariable(RequestParams.USER_ID.value)
                    .let { friendId ->
                        userService.addFriend(friendId)
                                .flatMap { done ->
                                    if (done) {
                                        ServerResponse.ok().build()
                                    } else
                                        ServerResponse.badRequest().build()
                                }
                    }
                    .metricHandledRequest("/friend/add/{userId}")

    fun deleteFriend(request: ServerRequest): Mono<ServerResponse> =
            request.pathVariable(RequestParams.USER_ID.value).let { friendId ->
                userService.deleteFriend(friendId).flatMap {
                    ServerResponse.ok().build()
                }.onErrorResume {
                    ServerResponse.badRequest().build()
                }
            }
                    .metricHandledRequest("/friend/delete/{userId}")
}