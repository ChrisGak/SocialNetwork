package me.kristinasaigak.otus.service

import getCurrentUserId
import me.kristinasaigak.otus.repository.FriendRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
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
                        friendRepository.getFriendIds(currentUserId)
                                .map {
                                    it.userId
                                }.collectList()
                    }
}