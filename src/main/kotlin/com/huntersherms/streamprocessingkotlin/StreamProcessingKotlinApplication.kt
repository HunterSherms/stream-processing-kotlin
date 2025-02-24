package com.huntersherms.streamprocessingkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class StreamProcessingKotlinApplication

fun main(args: Array<String>) {
    runApplication<StreamProcessingKotlinApplication>(*args)
}
