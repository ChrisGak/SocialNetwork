package me.kristinasaigak.otus.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import com.mysql.jdbc.Driver
import javax.sql.DataSource

@Configuration
class DatabaseConfig(
        val dbProperties: DatabaseProperties
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    @Primary
    fun source() : DataSource {
        val ds = SimpleDriverDataSource()
        ds.apply {
            url = dbProperties.url
            username = dbProperties.username
            password = dbProperties.password
            ds.driver = Driver()
        }.also { logger.info("DB connected with ${dbProperties.host()}")}
        return ds
    }

    @Bean
    @Primary
    fun jdbcTemplate() : JdbcTemplate {
        return JdbcTemplate(source())
    }
}