package com.huntersherms.streamprocessingkotlin.config

import org.rocksdb.CompressionType
import org.rocksdb.Options
import org.rocksdb.RocksDB
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Configuration for RocksDB
 */
@Configuration
@ConfigurationProperties("rocksdb")
@Profile("example3")
data class RocksConfig(
    var createIfMissing: Boolean = true,
    var logFileNum: Long = 10,
    var path: String = "/example3",
) {

    @Bean
    fun options() =  Options().setCreateIfMissing(createIfMissing)
        .setCompressionType(CompressionType.LZ4_COMPRESSION)
        .setKeepLogFileNum(logFileNum)

    @Bean
    fun db(options: Options) = RocksDB.loadLibrary().let {
        RocksDB.open(options, path)
    }
}