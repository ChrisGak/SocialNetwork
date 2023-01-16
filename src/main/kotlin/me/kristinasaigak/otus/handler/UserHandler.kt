package me.kristinasaigak.otus.handler

import lombok.RequiredArgsConstructor
import me.kristinasaigak.otus.model.User
import me.kristinasaigak.otus.model.UserDto
import me.kristinasaigak.otus.model.security.AuthRequest
import me.kristinasaigak.otus.service.UserService
import me.kristinasaigak.otus.utils.hash
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
@RequiredArgsConstructor
class UserHandler(
    private val userService: UserService
) {

    fun search(request: ServerRequest?): Mono<ServerResponse?> {
        return userService.getAllUsers().collectList().flatMap { users ->
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    users.map { user ->
                        UserDto(
                            user.id, user.first_name, user.second_name,
                            user.age, user.biography, user.city
                        )
                    }
                )
        }
    }

    fun getUserById(request: ServerRequest): Mono<ServerResponse?> {
        return userService
            .findById(request.pathVariable("userId"))
            .flatMap { user ->
                println("User found = $user")
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                        UserDto(
                            user.id, user.first_name, user.second_name,
                            user.age, user.biography, user.city
                        )
                    )
            }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    fun login(request: ServerRequest): Mono<ServerResponse?> {
        return request.bodyToMono(AuthRequest::class.java)
            .flatMap { authRequest ->
                userService.findById(authRequest.id)
                    .flatMap { user ->
                        if (user.password != hash(authRequest.password)) {
                            ServerResponse.badRequest().build()
                        } else {
                            ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("Hi, you are just logged in as ${user.first_name} ${user.second_name}")
                        }
                    }
                    .switchIfEmpty(
                        ServerResponse.notFound().build()
                    )
            }
    }

    fun register(request: ServerRequest): Mono<ServerResponse?> {
        val userRequest: Mono<User> = request.bodyToMono(User::class.java)
        return userRequest
            .flatMap { userService.createUser(it) }
            .flatMap { user ->
                ServerResponse
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(user.id!!)
            }
    }
}