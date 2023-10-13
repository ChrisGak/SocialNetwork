package me.kristinasaigak.otus.config

import me.kristinasaigak.otus.model.dto.UnreadMessagesCountDto
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
@EnableConfigurationProperties(RedisProperties::class)
class RedisConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Long, UnreadMessagesCountDto> {
        logger.info("Init redis template")
        val template: RedisTemplate<Long, UnreadMessagesCountDto> = RedisTemplate<Long, UnreadMessagesCountDto>()
        template.setConnectionFactory(connectionFactory)
        // Add some specific configuration here. Key serializers, etc.
        return template
    }
}