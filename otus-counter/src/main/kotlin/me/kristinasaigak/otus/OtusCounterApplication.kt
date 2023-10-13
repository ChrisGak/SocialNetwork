package me.kristinasaigak.otus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class OtusCounterApplication

fun main(args: Array<String>) {
    runApplication<OtusCounterApplication>(*args)
}