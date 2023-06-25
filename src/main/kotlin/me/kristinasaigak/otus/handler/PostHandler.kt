package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.model.dto.PostDto
import me.kristinasaigak.otus.service.PostService
import me.kristinasaigak.otus.utils.RequestParams
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

    fun getPost(request: ServerRequest): Mono<ServerResponse> =
            request.queryParam(RequestParams.ID.value).let {
                if (it.isPresent) {
                    postService.getPost(it.get().toInt()).flatMap {
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(PostDto(text = it.text, author = it.authorUserId))
                    }
                            .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build())
                } else
                    ServerResponse.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build()
            }

    fun createPost(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono(String::class.java).let {
                return it.flatMap { postText -> postService.createPost(postText) }
                        .flatMap {
                            ServerResponse.status(HttpStatus.CREATED).build()
                        }
            }

    /**
     * Лента постов друзей
     */
    fun getPosts(request: ServerRequest): Mono<ServerResponse> {
        val offset = request.queryParam(RequestParams.OFFSET.value).let {
            if (it.isEmpty) 0 else it.get().toLong()
        }
        val limit = request.queryParam(RequestParams.LIMIT.value).let {
            if (it.isEmpty) 10 else it.get().toLong()
        }
        return postService.searchPosts(offset, limit).flatMap { posts ->
            logger.debug("Found posts size for current user: ${posts.size}, offset: $offset, limit: $limit")
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            posts.map {
                                PostDto(text = it.text, author = it.authorUserId)
                            }
                    )
        }
    }

    fun removePost(request: ServerRequest): Mono<ServerResponse> {
        request.queryParam(RequestParams.ID.value).let { postId ->
            return if (postId.isPresent) {
                postService.deletePost(postId.get().toInt()).flatMap {
                    ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("Успешно удален пост")
                }
            } else ServerResponse.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build()
        }
    }
}