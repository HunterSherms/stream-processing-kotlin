package com.huntersherms.streamprocessingkotlin.shared

/**
 * Generic configuration class for our various streams
 */
data class KafkaConfig(
    val consumerGroupName: String,
    val bootstrapServer: String,
    val concurrency: Int,
    val sourceTopic: String,
    val targetTopic: String?
)
