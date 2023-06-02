package me.kristinasaigak.otus.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * Пост пользователя
 */
@Table("post")
data class Post(
        @Id
        @Column("id")
        var id: Int? = null,
        @Column("text")
        var text: String,
        @Column("author_user_id")
        var authorUserId: Int? = null,
        @Version
        var version: Long? = null,
        @CreatedDate
        var createdDate: LocalDateTime? = null,
        @LastModifiedDate
        var lastModifiedDate: LocalDateTime? = null
)