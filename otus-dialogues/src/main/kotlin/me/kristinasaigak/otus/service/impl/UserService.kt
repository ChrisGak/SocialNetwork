package me.kristinasaigak.otus.service.impl

import me.kristinasaigak.otus.model.entity.User
import me.kristinasaigak.otus.repository.UserReplicaRepository
import me.kristinasaigak.otus.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class UserService(
        private val userRepository: UserRepository,
        private val userReplicaRepository: UserReplicaRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun getCurrentUser(userId: Int): Mono<User> {
        return userRepository.findById(userId)
    }

    fun findById(userId: String?): Mono<User> {
        return userReplicaRepository.findById(userId!!)
                .firstOrNull()
                .toMono()
    }
}