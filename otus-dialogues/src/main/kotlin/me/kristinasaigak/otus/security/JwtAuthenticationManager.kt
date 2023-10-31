package me.kristinasaigak.otus.security

import me.kristinasaigak.otus.model.security.Role
import me.kristinasaigak.otus.service.impl.JwtSigner
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(private val jwtSigner: JwtSigner) : ReactiveAuthenticationManager {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        logger.debug("Authenticate: ${authentication.principal}")
        return Mono.just(authentication)
                .map { jwtSigner.validateJwt(it.credentials as String) }
                .onErrorResume { Mono.empty() }
                .map { jws ->
                    UsernamePasswordAuthenticationToken(
                            jws.body.subject,
                            authentication.credentials as String,
                            mutableListOf(SimpleGrantedAuthority(Role.ROLE_USER.name))
                    )
                }
    }
}