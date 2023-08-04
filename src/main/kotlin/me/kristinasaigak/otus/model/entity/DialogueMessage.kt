package me.kristinasaigak.otus.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("dialogue")
data class DialogueMessage(
        @Id
        @Column("id")
        var id: Int? = null,
        @Column("from_user_id")
        var fromUserId: Int,
        @Column("to_user_id")
        var toUserId: Int,
        @Column("text")
        var text: String,
        @Column("created_date")
        var createdDate: LocalDateTime? = null,
        @Column("last_modified_date")
        var lastModifiedDate: LocalDateTime? = null
)
