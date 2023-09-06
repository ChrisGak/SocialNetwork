package me.kristinasaigak.otus.config

import com.fasterxml.jackson.databind.ObjectMapper
import me.kristinasaigak.otus.utils.TWEET_PUBLISHED_EXCHANGE
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableRabbit
@Configuration
class RabbitMQConfig(private val connectionFactory: ConnectionFactory) {
    @Bean
    fun rabbitTemplate(): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.setExchange(TWEET_PUBLISHED_EXCHANGE)
        rabbitTemplate.messageConverter = jsonMessageConverter()
        return rabbitTemplate
    }

    @Bean
    fun tweetFeedExchange(): TopicExchange = TopicExchange(TWEET_PUBLISHED_EXCHANGE)

    @Bean
    fun jsonMessageConverter(): MessageConverter = Jackson2JsonMessageConverter(ObjectMapper())
}