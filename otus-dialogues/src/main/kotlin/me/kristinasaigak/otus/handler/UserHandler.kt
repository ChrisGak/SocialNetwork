package me.kristinasaigak.otus.handler

import lombok.RequiredArgsConstructor
import me.kristinasaigak.otus.model.security.AuthRequest
import me.kristinasaigak.otus.model.security.AuthResponse
import me.kristinasaigak.otus.service.JwtSigner
import me.kristinasaigak.otus.service.UserService
import me.kristinasaigak.otus.utils.hash
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

                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Set-Cookie", authCookie.toString())
                                        .bodyValue(AuthResponse(token = token))
                            }
                        }
            }
            .switchIfEmpty(
                    ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
            )
}