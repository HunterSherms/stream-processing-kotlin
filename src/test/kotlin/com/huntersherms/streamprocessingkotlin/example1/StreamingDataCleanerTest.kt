package com.huntersherms.streamprocessingkotlin.example1

import com.huntersherms.streamprocessingkotlin.shared.CleanedUser
import com.huntersherms.streamprocessingkotlin.shared.CleanedUserSerde
import com.huntersherms.streamprocessingkotlin.shared.KafkaConfig
import com.huntersherms.streamprocessingkotlin.shared.User
import com.huntersherms.streamprocessingkotlin.shared.UserSerde
import com.huntersherms.streamprocessingkotlin.shared.toCleanedUser
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StreamingDataCleanerTest {

    private val config = KafkaConfig(
        consumerGroupName = "StreaminDataCleanerTest",
        bootstrapServer = "fake:bootstrap",
        concurrency = 1,
        sourceTopic = "users",
        targetTopic = "cleaned-users"
    )

    private val streamingDataCleaner = StreamingDataCleaner(config)

    /**
     * Run an in-memory mock of Kafka
     */
    private val testDriver = TopologyTestDriver(
        streamingDataCleaner.getTopology(),
        streamingDataCleaner.getStreamProperties())

    private val inputTopic: TestInputTopic<ByteArray, User> = testDriver.createInputTopic(
        config.sourceTopic,
        Serdes.ByteArray().serializer(),
        UserSerde().serializer()
    )

    private val outputTopic: TestOutputTopic<ByteArray, CleanedUser> = testDriver.createOutputTopic(
        config.targetTopic,
        Serdes.ByteArray().deserializer(),
        CleanedUserSerde().deserializer()
    )

    @Test
    fun `Test that user data is successfully cleaned`() {

        val userInputs = listOf(
            User(
                userId = "1",
                name = "Example Name 1",
                email = "example1@gmail.com",
                dob = LocalDate.of(1970, 1, 1),
                accountCreated = Instant.now().minus(1, ChronoUnit.DAYS)
            ),
            User(
                userId = "2",
                name = "Example Name 2",
                email = "example2@gmail.com",
                dob = LocalDate.of(1972, 2, 2),
                accountCreated = Instant.now().minus(2, ChronoUnit.DAYS)
            ),
            User(
                userId = "3",
                name = "Example Name 3",
                email = "example3@gmail.com",
                dob = LocalDate.of(1973, 3, 3),
                accountCreated = Instant.now().minus(3, ChronoUnit.DAYS)
            )
        )

        // Write values into the input topic
        userInputs.forEach {
            inputTopic.pipeInput(it)
        }

        // Read values from the output topic
        val outputs = outputTopic.readValuesToList()

        assertTrue(outputs.size == userInputs.size)

        userInputs.forEachIndexed { index, userInput ->
            assertEquals(userInput.toCleanedUser(), outputs[index])
        }
    }
}