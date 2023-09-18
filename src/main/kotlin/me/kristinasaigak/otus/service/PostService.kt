package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.entity.Post
import me.kristinasaigak.otus.repository.PostReactiveRepository
import me.kristinasaigak.otus.repository.PostRepository
import me.kristinasaigak.otus.utils.getCurrentUserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class PostService(
        private val postReactiveRepository: PostReactiveRepository,
        private val postRepository: PostRepository,
        private val cacheService: CacheService,
        private val rabbitQueueService: RabbitQueueService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createPost(postText: String): Mono<Void> {
        return getCurrentUserId()
                .flatMap { userId ->
                    logger.debug("Current user id: $userId")
                    postReactiveRepository.save(Post(text = postText, authorUserId = userId.toInt()))
                            .flatMap {
                                rabbitQueueService.publishPost(userId, it)
                            }
                            .then(cacheService.invalidateByAuthorId())
                }
    }

    fun getPost(id: Int): Mono<Post> =
            postReactiveRepository.findById(id)

    fun searchPosts(offset: Long? = 0, limit: Long? = 10): Mono<List<Post>> {
        return getCurrentUserId()
                .flatMap { userId ->
                    logger.debug("Current user id: $userId")
                    postRepository.feed(userId)
                            .stream().skip(offset!!).limit(limit!!).toList().toMono()
                }
    }

    fun deletePost(postId: Int): Mono<Void> =
            postReactiveRepository.deleteById(postId)
                    .then(cacheService.invalidateByAuthorId())
}