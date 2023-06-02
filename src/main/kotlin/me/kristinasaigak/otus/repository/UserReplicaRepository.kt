package me.kristinasaigak.otus.repository

import me.kristinasaigak.otus.model.entity.User
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserReplicaRepository(val jdbcTemplate: JdbcTemplate) {
    val userMapper = DataClassRowMapper(User::class.java)

    fun findById(id: String): List<User> {
        return jdbcTemplate.query("""
                    SELECT id, first_name, second_name, age, biography, city, password FROM "users" WHERE id = ?     
            """.trimIndent(), userMapper, id.toInt())
    }

    fun findByFirstNameAndSecondName(firstName: String, secondName: String): List<User> {
        return jdbcTemplate.query("""
                    SELECT u.id, u.first_name, u.second_name, u.age, u.biography, u.city, u.password FROM "users" u 
                    WHERE  u.first_name LIKE ? AND u.second_name LIKE ?
                    ORDER BY u.id ASC             
            """.trimIndent(), userMapper, "%$firstName%", "%$secondName%")
    }
}