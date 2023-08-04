package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.dto.SearchUsersDto
import me.kristinasaigak.otus.model.entity.FriendRelationship
import me.kristinasaigak.otus.model.entity.User
import me.kristinasaigak.otus.repository.FriendRepository
import me.kristinasaigak.otus.repository.UserReplicaRepository
import me.kristinasaigak.otus.repository.UserRepository
import me.kristinasaigak.otus.utils.getCurrentUserId
import me.kristinasaigak.otus.utils.hash
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Service
class UserService(
        private val userRepository: UserRepository,
        private val userReplicaRepository: UserReplicaRepository,
        private val friendRepository: FriendRepository,
        private val cacheService: CacheService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun createUser(user: User): Mono<User> {
        user.apply {
            password = if (user.password != null) {
                hash(user.password!!)
            } else null
        }.also {
            logger.info("Create user: $user")
            return userRepository.save(user)
        }
    }

    fun getCurrentUser(userId: Int): Mono<User> {
        return userRepository.findById(userId)
    }

    fun findById(userId: String?): Mono<User> {
        return userReplicaRepository.findById(userId!!)
                .firstOrNull()
                .toMono()
    }

    fun searchUsers(searchUsersDto: SearchUsersDto): Flux<User> {
        return userReplicaRepository.findByFirstNameAndSecondName(searchUsersDto.firstName, searchUsersDto.secondName).stream().toFlux()
    }

    fun addFriend(friendId: String): Mono<Boolean> {
        return getCurrentUserId()
                .flatMap { currentUserId ->
                    logger.debug("Current user id: $currentUserId")
                    if (currentUserId == friendId) {
                        logger.error("Current user can not add himself to friends: $friendId")
                        false.toMono()
                    } else {
                        userRepository.findById(friendId.toInt()).flatMap {
                            logger.debug("Friend user found: ${it.first_name} ${it.second_name}")
                            friendRepository.save(FriendRelationship(
                                    requesterUserId = currentUserId.toInt(),
                                    accepterUserId = friendId.toInt()
                            )).flatMap {
                                true.toMono()
                            }.doOnEach {
                                cacheService.invalidate(currentUserId)
                            }
                        }.switchIfEmpty {
                            logger.error("The friend user not found: $friendId")
                            false.toMono()
                        }
                    }
                }.switchIfEmpty {
                    logger.error("Current user not found")
                    false.toMono()
                }
                .onErrorResume(DataIntegrityViolationException::class.java) {
                    logger.info("The user $friendId is already added to friends")
                    true.toMono()
                }
    }

    fun deleteFriend(friendId: String): Mono<Void> =
            getCurrentUserId()
                    .flatMap { currentUserId ->
                        friendRepository.deleteByUserIdAndFriendId(currentUserId.toInt(), friendId.toInt())
                                .doOnNext {
                                    logger.debug("The friend $friendId is removed for current user: $currentUserId")
                                }
                                .doOnError {
                                    logger.error("The friend is not removed for current user: $currentUserId, ex: $it")
                                }
                                .doOnEach {
                                    cacheService.invalidate(currentUserId)
                                }

                    }
}