package com.huntersherms.streamprocessingkotlin.example2

import com.huntersherms.streamprocessingkotlin.config.KafkaConfig
import com.huntersherms.streamprocessingkotlin.example1.StreamingIntoLocalStorage
import com.huntersherms.streamprocessingkotlin.shared.CleanedUser
import com.huntersherms.streamprocessingkotlin.shared.CleanedUserSerde
import com.huntersherms.streamprocessingkotlin.shared.LocalCleanedUserStore
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit


@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@ActiveProfiles("example2")
class StreamingIntoMemoryTest {

    private val config = KafkaConfig(
        consumerGroupName = "StreamingIntoLocalStorageTest",
        bootstrapServer = "fake:bootstrap",
        concurrency = 1,
        sourceTopic = "cleaned-users",
        targetTopic = null
    )

    @Autowired
    lateinit var store: LocalCleanedUserStore

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val streams by lazy {
        StreamingIntoLocalStorage(config, store)
    }

    private val testDriver by lazy {
        TopologyTestDriver(
            streams.getTopology(),
            streams.getStreamProperties())
    }

    private val inputTopic by lazy {
        testDriver.createInputTopic(
            config.sourceTopic,
            Serdes.String().serializer(),
            CleanedUserSerde().serializer()
        )
    }

    @Test
    fun `CleanedUser objects written to kafka can be retrieved via the api single and batch endpoints`() {

        val userInputs = listOf(
            CleanedUser(
                userId = "1",
                accountCreated = Instant.now().minus(1, ChronoUnit.DAYS)
            ),
            CleanedUser(
                userId = "2",
                accountCreated = Instant.now().minus(2, ChronoUnit.DAYS)
            ),
            CleanedUser(
                userId = "3",
                accountCreated = Instant.now().minus(3, ChronoUnit.DAYS)
            )
        )

        // Write values into the input topic
        userInputs.forEach {
            inputTopic.pipeInput(it.userId, it)
        }

        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.accountCreated").value(userInputs[0].accountCreated.toString()))

        mockMvc.perform(
            get("/users?ids=3,2"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value("3"))
            .andExpect(jsonPath("$[1].userId").value("2"))
    }
}