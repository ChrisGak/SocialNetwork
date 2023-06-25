package me.kristinasaigak.otus.repository

import me.kristinasaigak.otus.model.entity.Post
import org.springframework.cache.annotation.Cacheable
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class PostRepository(
        val jdbcTemplate: JdbcTemplate
) {
    val postMapper = DataClassRowMapper(Post::class.java)

    companion object {
        private const val FEED_SIZE = 1000
    }

    @Cacheable(value = ["friendPosts"], key = "#userId")
    fun feed(userId: String, feedSize: Int? = FEED_SIZE): List<Post> {
        return jdbcTemplate.query(
                """
                SELECT p.id, p.text, p.author_user_id, p.version, p.created_date, p.last_modified_date FROM "post" p 
                JOIN "friends" f ON f.accepter_user_id = p.author_user_id
                WHERE f.requester_user_id = ?
                ORDER BY p.id ASC
                LIMIT ?
            """.trimIndent(),
                postMapper,
                userId.toInt(),
                feedSize
        )
    }
}