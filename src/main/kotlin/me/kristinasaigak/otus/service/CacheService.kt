package me.kristinasaigak.otus.service

import getCurrentUserId
import me.kristinasaigak.otus.config.CacheConfig
import me.kristinasaigak.otus.repository.FriendRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

@Service
class CacheService(
        private val cacheManager: CacheManager,
        private val friendRepository: FriendRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun invalidate(userId: String) {
        logger.info("Invalidate cache for user: $userId")
        cacheManager.getCache(CacheConfig.CACHE_NAME)?.also {
            logger.debug("Cache name: ${it.name}")
            it.evictIfPresent(userId)
        }
    }

    fun invalidateByAuthorId() =
            getCurrentUserId()
                    .flatMap { currentUserId ->
                        logger.info("Invalidate cache for friends of current user: $currentUserId")
                        friendRepository.getFriendIds(currentUserId)
                                .map {
                                    invalidate(it.userId)
                                }.then()
                    }
}