package me.kristinasaigak.otus.model.entity.tarantool

import org.springframework.data.annotation.Id
import org.springframework.data.tarantool.core.mapping.Field
import org.springframework.data.tarantool.core.mapping.Tuple
import java.util.UUID

@Tuple("dialogue")
data class TarantoolDialogueMessage(
        @Id
        var id: UUID? = null,
        @Field(name = "from_user_id")
        var fromUserId: String,
        @Field("to_user_id")
        var toUserId: String,
        @Field("text")
        var text: String,
)
