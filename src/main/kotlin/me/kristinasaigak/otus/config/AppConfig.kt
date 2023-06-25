package me.kristinasaigak.otus.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener

@Configuration
class AppConfig {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(classes = [ApplicationReadyEvent::class])
    fun onStartup() {
        logger.info("Module started")
    }

    @EventListener(classes = [ContextClosedEvent::class])
    fun onShutdown() {
        logger.info("Module shutdown")
    }
}