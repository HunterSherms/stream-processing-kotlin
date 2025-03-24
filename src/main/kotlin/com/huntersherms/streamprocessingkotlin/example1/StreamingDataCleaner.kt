package com.huntersherms.streamprocessingkotlin.example1

import com.huntersherms.streamprocessingkotlin.shared.CleanedUser
import com.huntersherms.streamprocessingkotlin.shared.CleanedUserSerde
import com.huntersherms.streamprocessingkotlin.config.KafkaConfig
import com.huntersherms.streamprocessingkotlin.shared.UserSerde
import com.huntersherms.streamprocessingkotlin.shared.toCleanedUser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import org.springframework.context.SmartLifecycle
import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Example 1 is a simple mapping Kafka stream that strips identifying information from
 * [com.huntersherms.streamprocessingkotlin.shared.User] objects.
 * This code is reading [User] objects from one kafka topic and writing [CleanedUser] objects back out
 * to another Kafka topic.
 */
class StreamingDataCleaner(
    private val config: KafkaConfig
): SmartLifecycle {

    private val log = KotlinLogging.logger { }

    private val running = AtomicBoolean(false)

    private var streams: KafkaStreams? = null

    override fun start() {
        log.info { "Starting StreamingDataCleaner..." }
        streams = startKStream()
        log.info { "StreamingDataCleaner started." }
        running.set(true)
    }

    override fun stop() {
        log.info { "Stopping StreamingDataCleaner." }
        shutdown()
    }

    override fun isRunning(): Boolean = running.get()

    private fun shutdown() {
        streams?.close()
        running.set(false)
    }

    /**
     * Wires our topology and properties together.
     *
     * These three methods are kept separate because the other two can be used
     * directly in tests to test your stream logic.
     */
    private fun startKStream(): KafkaStreams {

        // Set up Kafka configuration
        val props = getStreamProperties()

        // Set up the KStreams logic
        val topology = getTopology()

        val streams = KafkaStreams(topology, props)

        streams.start()

        return streams
    }

    internal fun getTopology(): Topology {
        val builder = StreamsBuilder()

        builder.stream(
            config.sourceTopic,
            Consumed.with(
                Serdes.ByteArray(),
                UserSerde()
            )
        )
        .filter { _, value ->
            // Filtering null values may be useful here, maybe the source topic is compacting.
            value != null
        }
        .map { key, user ->
            // Map our user to a cleaned user, wrap in a KeyValue for KStreams
            val cleanedUser = user.toCleanedUser()
            KeyValue(cleanedUser.userId, cleanedUser)
        }
        .to(
            config.targetTopic,
            Produced
                .with(
                    Serdes.String(),
                    CleanedUserSerde()) // Tell KStreams how to serialize our CleanedUser
        )

        return builder.build()
    }

    internal fun getStreamProperties(): Properties {
        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = config.consumerGroupName
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = config.bootstrapServer
        props[StreamsConfig.NUM_STREAM_THREADS_CONFIG] = config.concurrency
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return props
    }
}