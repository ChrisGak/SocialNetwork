package me.kristinasaigak.otus.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("friends")
data class FriendRelationship(
        @Id
        @Column("id")
        var id: String? = null,
        @Column("requester_user_id")
        var requesterUserId: String,
        @Column("accepter_user_id")
        var accepterUserId: String,
        @CreatedDate
        @Column("created_date")
        var createdDate: LocalDateTime? = null
)