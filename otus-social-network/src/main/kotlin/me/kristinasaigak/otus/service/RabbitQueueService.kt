package me.kristinasaigak.otus.service

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import me.kristinasaigak.otus.model.dto.PostDto
import me.kristinasaigak.otus.model.entity.Post
import me.kristinasaigak.otus.utils.TWEET_PUBLISHED_EXCHANGE
import me.kristinasaigak.otus.utils.userFeedQueueName
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@Service
class RabbitQueueService(private val rabbitTemplate: RabbitTemplate,
                         private val connectionFactory: ConnectionFactory,
                         private val friendService: FriendService) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val channelMap = ConcurrentHashMap<String, Channel>()
    private var currentUserConsumerTags = ConcurrentHashMap<String, String>()

    fun publishPost(userId: String, post: Post): Mono<Void> =
            Mono.fromCallable {
                createAndBindQueueIfNotExists(userId)
                val payload = PostDto(postId = post.id.toString(), postText = post.text, author_user_id = post.authorUserId.toString())
                logger.info("Send post of author: $userId to exchange: $TWEET_PUBLISHED_EXCHANGE. Post: $payload")
                rabbitTemplate.convertAndSend(TWEET_PUBLISHED_EXCHANGE, userId, payload)
            }
                    .then()

    fun initPostFeed(currentUserId: String): Mono<*> {
        logger.info("Create current user's feed queue if not exists")
        createAndBindQueueIfNotExists(currentUserId)
        return friendService.getFriends(currentUserId)
                .filter { !it.isNullOrBlank() }
                .map { friendId ->
                    createAndBindQueueIfNotExists(friendId)
                }
                .collectList()
    }
    fun startConsuming(currentUserId: String): Mono<Sinks.Many<String>> {
        logger.info("Subscribe to post feed for current user: $currentUserId")
        currentUserConsumerTags = ConcurrentHashMap<String, String>()
        val localSink: Sinks.Many<String> = Sinks.many().multicast().onBackpressureBuffer()
        return friendService.getFriends(currentUserId)
                .filter { !it.isNullOrBlank() }
                .map { friendId ->
                    createAndBindQueueIfNotExists(friendId)
                    val queueName = userFeedQueueName(friendId)
                    logger.info("Subscribe to topic: $queueName of current user's friends feed")
                    val channel = getUsersChannel(currentUserId)

                    val callback = object : DefaultConsumer(channel) {
                        @Throws(IOException::class)
                        override fun handleDelivery(consumerTag: String?,
                                                    envelope: Envelope?, properties: AMQP.BasicProperties?,
                                                    body: ByteArray) {
                            val message = String(body)
                            logger.info("Received message: '$message', consumerTag: $consumerTag, routingKey: ${envelope?.routingKey}")
                            localSink.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST)
                        }
                    }
                    logger.info("Start listen post events of queue: $queueName")
                    val consumerTag = channel.basicConsume(queueName, true, callback)
                    logger.info("Save consumerTag for currentUserId: $currentUserId, friendId: $friendId, consumerTag: $consumerTag")
                    currentUserConsumerTags[currentUserId] = consumerTag
                }
                .doOnNext {
                    logger.debug("currentUserConsumerTags: $currentUserConsumerTags")
                    logger.debug("channelMap: $channelMap")
                }
                .collectList()
                .thenReturn(
                        localSink
                )
    }
    fun stopConsuming(currentUserId: String) {
        channelMap[currentUserId]?.let { channel ->
            logger.debug("Channel number: ${channel.channelNumber} for user: $currentUserId")
            logger.debug("consumerTags: $currentUserConsumerTags")
            currentUserConsumerTags.values.forEach {
                logger.info("Unsubscribe post feed for currentUserId: $currentUserId by consumerTag: $it")
                channel.basicCancel(it)
            }
        }
        currentUserConsumerTags.clear()

        logger.debug("channelMap: $channelMap")
    }
    fun unsubscribeTopic(currentUserId: String, exFriendId: String) =
            channelMap[currentUserId]?.let { channel ->
                logger.debug("consumerTags: $currentUserConsumerTags")
                val consumerTag = currentUserConsumerTags[exFriendId]
                logger.info("Unsubscribe ex-friend's post feed for currentUserId: $currentUserId, friendId: $exFriendId by consumerTag: $consumerTag")
                channel.basicCancel(consumerTag)
            }

    private fun getUsersChannel(userId: String): Channel {
        var channel = channelMap[userId]

        if (channel == null) {
            channel = connectionFactory.createConnection().createChannel(false)
            channelMap[userId] = channel
        }

        logger.debug("Channel number: ${channel.channelNumber} for user: $userId")
        return channel
    }

    private fun createAndBindQueueIfNotExists(userId: String) =
            getUsersChannel(userId).let { channel ->
                val queueName = userFeedQueueName(userId)
                channel.queueDeclare(queueName, true, false, false, null)
                logger.info("Create $queueName queue and bind to $TWEET_PUBLISHED_EXCHANGE exchange if not exists")
                channel.queueBind(queueName, TWEET_PUBLISHED_EXCHANGE, userId)
            }
}