package me.kristinasaigak.otus.repository

import me.kristinasaigak.otus.model.entity.DialogueMessage
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface DialogueRepository : R2dbcRepository<DialogueMessage, Int> {
    @Query("SELECT id, text, from_user_id, to_user_id, created_date, last_modified_date FROM \"dialogue\" " +
            "WHERE (from_user_id = :#{#currentUserId} AND to_user_id = :#{#anotherUserId} " +
            "   OR from_user_id = :#{#anotherUserId} AND to_user_id = :#{#currentUserId} )  " +
            "ORDER BY id ASC")
    fun findAllDialogueMessagesByUsers(@Param("currentUserId") currentUserId: Int,
                                       @Param("anotherUserId") anotherUserId: Int): Flux<DialogueMessage>
}