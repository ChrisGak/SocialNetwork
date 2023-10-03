package me.kristinasaigak.otus.handler

import me.kristinasaigak.otus.service.RabbitQueueService
import me.kristinasaigak.otus.utils.MetricTag
import me.kristinasaigak.otus.utils.MetricTitle
import me.kristinasaigak.otus.utils.getCurrentUserId
import me.kristinasaigak.otus.utils.metric
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class PostWebSocketController(private val rabbitQueueService: RabbitQueueService) : WebSocketHandler {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun handle(webSocketSession: WebSocketSession): Mono<Void> =
            getCurrentUserId()
                    .flatMap { currentUserId ->
                        rabbitQueueService.startConsuming(currentUserId)
                                .flatMap {
                                    logger.info("Start websocket handing: ${webSocketSession.id} for current user id: $currentUserId")
                                    logger.debug("HandshakeInfo: ${webSocketSession.handshakeInfo}")
                                    val f = it.asFlux()
                                            .map { msg ->
                                                logger.info("Send message to websocket feed: $msg to user: $currentUserId")
                                                webSocketSession.textMessage(msg)
                                            }
                                    webSocketSession.send(f).doFinally {
                                        logger.debug("On connection close")
                                        rabbitQueueService.stopConsuming(currentUserId)
                                    }
                                }
                    }
                    .metric(MetricTitle.WEBSOCKET_REQUEST_HANDLE.value, MetricTag.URL.value, "/post/feed/posted")
}