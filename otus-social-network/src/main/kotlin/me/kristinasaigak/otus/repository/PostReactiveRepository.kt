package me.kristinasaigak.otus.repository

import me.kristinasaigak.otus.model.entity.Post
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface PostReactiveRepository : R2dbcRepository<Post, Int>