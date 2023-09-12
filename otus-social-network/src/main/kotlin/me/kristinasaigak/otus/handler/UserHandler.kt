package me.kristinasaigak.otus.handler

import lombok.RequiredArgsConstructor
import me.kristinasaigak.otus.model.dto.SearchUsersDto
import me.kristinasaigak.otus.model.dto.UserDto
import me.kristinasaigak.otus.model.entity.User
import me.kristinasaigak.otus.model.security.AuthRequest
import me.kristinasaigak.otus.model.security.AuthResponse
import me.kristinasaigak.otus.service.JwtSigner
import me.kristinasaigak.otus.service.RabbitQueueService
import me.kristinasaigak.otus.service.UserService
import me.kristinasaigak.otus.utils.RequestParams
import me.kristinasaigak.otus.utils.hash
import me.kristinasaigak.otus.utils.metricHandledRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
@RequiredArgsConstructor
class UserHandler(
        private val userService: UserService,
        private val jwtSigner: JwtSigner,
        private val rabbitQueueService: RabbitQueueService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun login(request: ServerRequest): Mono<ServerResponse> = request.bodyToMono(AuthRequest::class.java)
            .flatMap { authRequest ->
                userService.getCurrentUser(authRequest.id)
                        .flatMap { user ->
                            if (user.password != hash(authRequest.password)) {
                                ServerResponse.badRequest().build()
                            } else {
                                val token = jwtSigner.createJwt(user.id.toString())
                                val authCookie = ResponseCookie.fromClientResponse("X-Auth", token)
                                        .maxAge(3600)
                                        .httpOnly(true)
                                        .path("/")
                                        .secure(false) // в продакшене должно быть true.
                                        .build()
                                logger.info("User logged in: $user")
                                logger.debug("Token: $token")
                                logger.debug("AuthCookie: $authCookie")

                                rabbitQueueService.initPostFeed(user.id.toString()).then(
                                        ServerResponse
                                                .status(HttpStatus.OK)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Set-Cookie", authCookie.toString())
                                                .bodyValue(AuthResponse(token = token))
                                )
                            }
                        }
            }
            .switchIfEmpty(
                    ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
            )
            .metricHandledRequest("/login")

    fun register(request: ServerRequest): Mono<ServerResponse> {
        val userRequest: Mono<User> = request.bodyToMono(User::class.java)
        return userRequest
                .flatMap { userService.createUser(it) }
                .flatMap { user ->
                    ServerResponse
                            .status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(user.id!!)
                }
                .metricHandledRequest("/user/register")
    }

    fun getUserById(request: ServerRequest): Mono<ServerResponse> = userService
            .findById(request.pathVariable(RequestParams.USER_ID.value))
            .flatMap { user ->
                logger.debug("User found = $user")
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
            .metricHandledRequest("/user/get/{userId}")

    fun search(request: ServerRequest): Mono<ServerResponse> {
        logger.info("The users search request received with query params: ${request.queryParams()}")
        return userService.searchUsers(SearchUsersDto(
                firstName = request.queryParam(RequestParams.FIRST_NAME.value).orElse(""),
                secondName = request.queryParam(RequestParams.SECOND_NAME.value).orElse("")
        )).collectList().flatMap { users ->
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
                .metricHandledRequest("/user/search")
    }
}