package me.kristinasaigak.otus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableR2dbcAuditing
class SocialNetworkApplication

fun main(args: Array<String>) {
    runApplication<SocialNetworkApplication>(*args)
}