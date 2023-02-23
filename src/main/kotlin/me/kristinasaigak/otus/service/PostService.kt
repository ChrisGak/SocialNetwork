package me.kristinasaigak.otus.service

import me.kristinasaigak.otus.model.entity.Post
import me.kristinasaigak.otus.repository.PostRepository
import me.kristinasaigak.otus.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Component
class PostService(
        private val postRepository: PostRepository,
        private val userRepository: UserRepository,
        private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createPost(postText: String): Mono<Post> {
        return ReactiveSecurityContextHolder.getContext()
                .map {
                    it.authentication.principal
                }
                .flatMap { userId ->
                    logger.debug("Current user id: $userId")
                    userRepository.findById(userId.toString()).flatMap { author ->
                        postRepository.save(Post(text = postText, authorId = userId.toString()))
                    }
                }
    }

    fun getPost(id: String): Mono<Post> =
            postRepository.findById(id)

    fun searchPosts(offset: Int? = 0, limit: Int? = 10): Flux<Post> {
        return userService.getFriends()
                .map { friends ->
                    logger.debug("Friends: $friends")

                }.toFlux().flatMap { postRepository.findAll() }
    }

    fun deletePost(postId: String): Mono<Void> =
            postRepository.deleteById(postId)
}