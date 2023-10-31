package me.kristinasaigak.otus.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ApiConfig(
        private val appProperties: AppProperties
) {

    @Bean
    fun counterServiceClient(): WebClient = WebClient.create(appProperties.counterServiceHost)
}