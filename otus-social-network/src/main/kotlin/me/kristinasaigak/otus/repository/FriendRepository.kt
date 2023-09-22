package me.kristinasaigak.otus.repository

import me.kristinasaigak.otus.model.entity.Friend
import me.kristinasaigak.otus.model.entity.FriendRelationship
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface FriendRepository : R2dbcRepository<FriendRelationship, String> {

    @Query(
        "SELECT DISTINCT user_id FROM (SELECT requester_user_id as user_id FROM \"friends\" WHERE accepter_user_id = :#{#userId} " +
                "UNION ALL SELECT accepter_user_id as user_id from friends WHERE requester_user_id = :#{#userId}) as result"
    )
    fun getFriendIds(@Param("userId") userId: Int): Flux<Friend>

    @Query(
        "DELETE FROM \"friends\" WHERE (requester_user_id = :#{#userId} AND accepter_user_id = :#{#friendId}) " +
                "OR (requester_user_id = :#{#friendId} AND accepter_user_id = :#{#userId})"
    )
    fun deleteByUserIdAndFriendId(@Param("userId") userId: Int, @Param("friendId") friendId: Int): Mono<Void>
}