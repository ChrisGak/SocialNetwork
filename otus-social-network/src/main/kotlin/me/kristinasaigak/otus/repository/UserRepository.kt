package me.kristinasaigak.otus.repository

import me.kristinasaigak.otus.model.entity.User
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface UserRepository : R2dbcRepository<User, Int> {
    @Query("SELECT u.id, u.first_name, u.second_name, u.age, u.biography, u.city FROM \"users\" " +
            "WHERE u.first_name LIKE concat('%',:#{#firstName},'%') AND u.second_name LIKE concat('%',:#{#secondName},'%')")
    fun findByFirstNameAndSecondName(@Param("firstName") firstName: String, @Param("secondName") secondName: String): Flux<User>
}