package me.kristinasaigak.otus.repository

import me.kristinasaigak.otus.model.dto.UnreadMessagesCountDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RedisRepository(
        private val redisTemplate: RedisTemplate<Long, UnreadMessagesCountDto>
)