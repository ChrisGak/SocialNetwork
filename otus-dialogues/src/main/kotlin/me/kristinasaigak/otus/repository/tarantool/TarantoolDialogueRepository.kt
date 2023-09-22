package me.kristinasaigak.otus.repository.tarantool

import me.kristinasaigak.otus.model.entity.tarantool.TarantoolDialogueMessage
import org.springframework.data.repository.query.Param
import org.springframework.data.tarantool.repository.Query
import org.springframework.data.tarantool.repository.TarantoolRepository
import org.springframework.data.tarantool.repository.TarantoolSerializationType
import org.springframework.stereotype.Repository

@Repository
interface TarantoolDialogueRepository : TarantoolRepository<TarantoolDialogueMessage, Int> {

    @Query(function = "saveMessage", output = TarantoolSerializationType.AUTO)
    fun saveMessage(
            @Param("fromUserId") fromUserId: String,
            @Param("toUserId") toUserId: String,
            @Param("text") text: String,
    )

    @Query(function = "find_all_messages_by_users", output = TarantoolSerializationType.TUPLE)
    fun findAllDialogueMessagesByUsers(@Param("currentUserId") currentUserId: String,
                                       @Param("anotherUserId") anotherUserId: String): List<TarantoolDialogueMessage>
}