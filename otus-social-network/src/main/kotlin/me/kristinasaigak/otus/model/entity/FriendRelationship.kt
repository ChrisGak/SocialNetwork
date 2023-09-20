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
        var id: Int? = null,
        @Column("requester_user_id")
        var requesterUserId: Int,
        @Column("accepter_user_id")
        var accepterUserId: Int,
        @CreatedDate
        @Column("created_date")
        var createdDate: LocalDateTime? = null
)