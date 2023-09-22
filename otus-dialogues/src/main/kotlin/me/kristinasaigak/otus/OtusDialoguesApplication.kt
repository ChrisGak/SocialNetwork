package me.kristinasaigak.otus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class OtusDialoguesApplication

fun main(args: Array<String>) {
    runApplication<OtusDialoguesApplication>(*args)
}