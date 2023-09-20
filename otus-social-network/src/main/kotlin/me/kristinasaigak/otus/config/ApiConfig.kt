package me.kristinasaigak.otus.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ApiConfig(
        private val otusAppProperties: OtusAppProperties
) {

    @Bean
    fun dialoguesClient(): WebClient = WebClient.create(otusAppProperties.dialoguesHost)
}