package me.kristinasaigak.otus.repository

import me.kristinasaigak.otus.model.User
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository: R2dbcRepository<User, String>