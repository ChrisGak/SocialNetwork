package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.User
import me.kristinasaigak.otus.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(user: User): Mono<User> {
        return userRepository.save(user)
    }

    fun getAllUsers(): Flux<User> {
       return userRepository.findAll()
    }

    fun findById(userId: String?): Mono<User> {
       return userRepository.findById(userId!!)
    }
}