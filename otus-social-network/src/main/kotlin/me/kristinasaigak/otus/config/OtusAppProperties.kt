package me.kristinasaigak.otus.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "otus")
data class OtusAppProperties(
        val dialoguesHost: String
)
