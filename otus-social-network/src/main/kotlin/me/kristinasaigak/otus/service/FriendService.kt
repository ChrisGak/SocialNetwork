package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.repository.FriendRepository
import me.kristinasaigak.otus.utils.getCurrentUserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FriendService(
        private val friendRepository: FriendRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getFriends(): Mono<List<String>> =
            getCurrentUserId()
                    .flatMap { currentUserId ->
                        logger.debug("Current user id: $currentUserId")
                        friendRepository.getFriendIds(currentUserId.toInt())
                                .map {
                                    it.userId.toString()
                                }.collectList()
                    }

    fun getFriends(currentUserId: String): Flux<String> =
            friendRepository.getFriendIds(currentUserId.toInt())
                    .map {
                        it.userId.toString()
                    }
}