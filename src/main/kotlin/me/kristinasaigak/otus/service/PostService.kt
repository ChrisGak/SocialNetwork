package me.kristinasaigak.otus.service

import getCurrentUserId
import me.kristinasaigak.otus.model.entity.Post
import me.kristinasaigak.otus.repository.PostReactiveRepository
import me.kristinasaigak.otus.repository.PostRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class PostService(
        private val postReactiveRepository: PostReactiveRepository,
        private val postRepository: PostRepository,
        private val cacheService: CacheService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createPost(postText: String): Mono<Void> {
        return getCurrentUserId()
                .flatMap { userId ->
                    logger.debug("Current user id: $userId")
                    postReactiveRepository.save(Post(text = postText, authorUserId = userId.toString()))
                            .then(cacheService.invalidateByAuthorId())
                }
    }

    fun getPost(id: String): Mono<Post> =
            postReactiveRepository.findById(id)

    fun searchPosts(offset: Long? = 0, limit: Long? = 10): Mono<List<Post>> {
        return getCurrentUserId()
                .flatMap { userId ->
                    logger.debug("Current user id: $userId")
                    postRepository.feed(userId)
                            .stream().skip(offset!!).limit(limit!!).toList().toMono()
                }
    }

    fun deletePost(postId: String): Mono<Void> =
            postReactiveRepository.deleteById(postId)
                    .then(cacheService.invalidateByAuthorId())
}