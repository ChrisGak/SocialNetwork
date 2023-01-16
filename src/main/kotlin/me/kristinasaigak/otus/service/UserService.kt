package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.User
import me.kristinasaigak.otus.repository.UserRepository
import me.kristinasaigak.otus.utils.hash
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(user: User): Mono<User> {
        user.apply {
            password = hash(user.password)
        }.also {
            return userRepository.save(user)
        }
    }

    fun getAllUsers(): Flux<User> {
        return userRepository.findAll()
    }

    fun findById(userId: String?): Mono<User> {
        return userRepository.findById(userId!!)
    }
}