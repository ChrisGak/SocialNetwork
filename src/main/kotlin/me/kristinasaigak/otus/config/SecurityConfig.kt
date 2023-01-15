package me.kristinasaigak.otus.config

import me.kristinasaigak.otus.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val userRepository: UserRepository
) {

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        return http
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers("/login").permitAll()
            .pathMatchers("/user/register").permitAll()
            // todo later
            // .anyExchange().authenticated()
            .anyExchange().permitAll()
            .and()
            .httpBasic()
            .and()
            .formLogin().disable()
            .build()
    }

//    @Bean
//    fun userDetailsService(): ReactiveUserDetailsService? {
//        return ReactiveUserDetailsService{ id: String -> userRepository.findUserDetails(id) }
//    }

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService {
        val user: UserDetails = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build()
        return MapReactiveUserDetailsService(user)
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}