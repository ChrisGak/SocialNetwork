package me.kristinasaigak.otus.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@EnableConfigurationProperties
class DatabaseProperties {

    lateinit var username: String
    lateinit var password: String
    lateinit var url: String
    lateinit var replicaUrl: String

    fun host(): String {
        return "$username:$password@$url"
    }

    fun replica(): String {
        return "$username:$password@$replicaUrl"
    }
}