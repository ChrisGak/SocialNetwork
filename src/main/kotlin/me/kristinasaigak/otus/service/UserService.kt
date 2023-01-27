package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.SearchUsersDto
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
            password = if (user.password != null) {
                hash(user.password!!)
            } else null
        }.also {
            return userRepository.save(user)
        }
    }

    fun findById(userId: String?): Mono<User> {
        return userRepository.findById(userId!!)
    }

    fun searchUsers(searchUsersDto: SearchUsersDto): Flux<User> {
        return userRepository.findByFirstNameAndSecondName(searchUsersDto.firstName, searchUsersDto.secondName)
    }
}