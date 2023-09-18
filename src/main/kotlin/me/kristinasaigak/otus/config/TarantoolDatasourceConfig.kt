package me.kristinasaigak.otus.config

import io.tarantool.driver.api.TarantoolServerAddress
import me.kristinasaigak.otus.repository.tarantool.TarantoolDialogueRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.tarantool.config.AbstractTarantoolDataConfiguration
import org.springframework.data.tarantool.repository.config.EnableTarantoolRepositories


@Configuration
@EnableTarantoolRepositories(basePackageClasses = [TarantoolDialogueRepository::class])
class TarantoolDatasourceConfig : AbstractTarantoolDataConfiguration() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${tarantool.host}")
    protected var host: String? = null

    @Value("\${tarantool.port}")
    protected var port = 0

    @Value("\${tarantool.username}")
    protected var username: String? = null

    @Value("\${tarantool.password}")
    protected var password: String? = null

    override fun tarantoolServerAddress(): TarantoolServerAddress {
        logger.debug("Init TarantoolServerAddress")
        return TarantoolServerAddress(host, port)
    }
}