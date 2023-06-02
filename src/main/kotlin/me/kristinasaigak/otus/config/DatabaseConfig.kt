package me.kristinasaigak.otus.config

import org.postgresql.Driver
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import javax.sql.DataSource

@Configuration
class DatabaseConfig(
        val dbProperties: DatabaseProperties
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    @Primary
    fun source(): DataSource {
        logger.debug("Init connection to master db node")
        val ds = SimpleDriverDataSource()
        ds.apply {
            url = dbProperties.url
            username = dbProperties.username
            password = dbProperties.password
            ds.driver = Driver()
        }.also { logger.info("DB connected to master db node with ${dbProperties.host()}") }
        return ds
    }

    @Bean
    fun sourceReplica(): DataSource {
        logger.debug("Init connection to replica db node")
        val ds = SimpleDriverDataSource()
        ds.apply {
            url = dbProperties.replicaUrl
            username = dbProperties.username
            password = dbProperties.password
            ds.driver = Driver()
        }.also { logger.info("DB connected to replica db node with ${dbProperties.replica()}") }
        return ds
    }

    @Bean
    @Primary
    fun jdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(source())
    }

    @Bean
    fun replicaJdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(sourceReplica())
    }
}