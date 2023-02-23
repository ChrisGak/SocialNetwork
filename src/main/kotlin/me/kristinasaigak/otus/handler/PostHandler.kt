package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.service.PostService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class PostHandler(
        private val postService: PostService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun getPost(request: ServerRequest): Mono<ServerResponse> {
        val postId = request.queryParam("id")
        return if (postId.isPresent) {
            postService.getPost(postId.get()).flatMap {
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(it)
            }

        } else {
            ServerResponse.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build()
        }
    }

    fun createPost(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(String::class.java).let {
                return it.flatMap { postText -> postService.createPost(postText) }
                        .flatMap {
                            ServerResponse.status(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(it.id!!)
                        }
            }

    /**
     * Лента постов друзей
     */
    fun getPosts(request: ServerRequest): Mono<ServerResponse> {
        val offset: Int = request.queryParam("offset").let {
            if (it.isEmpty) 0 else it.toString().toInt()
        }
        val limit = request.queryParam("limit").let {
            if (it.isEmpty) 10 else it.toString().toInt()
        }
        return postService.searchPosts(offset, limit).collectList().flatMap { posts ->
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            posts
                    )
        }
    }

    fun removePost(request: ServerRequest): Mono<ServerResponse> {
        val postId = request.queryParam("id")
        return if (postId.isPresent) {
            postService.deletePost(postId.get()).flatMap {
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("Успешно удален пост")
            }
        } else ServerResponse.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build()
    }
}