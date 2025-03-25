package com.huntersherms.streamprocessingkotlin.example3

import com.huntersherms.streamprocessingkotlin.shared.CleanedUser
import com.huntersherms.streamprocessingkotlin.shared.CleanedUserSerde
import com.huntersherms.streamprocessingkotlin.shared.LocalCleanedUserStore
import org.rocksdb.RocksDB
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("example3")
class OnDiskLocalStore(
    private val rocksDB: RocksDB
): LocalCleanedUserStore {

    private val deserializer = CleanedUserSerde().deserializer()

    override fun getById(id: String): CleanedUser? {
        return rocksDB.get(id.toByteArray())?.let { deserializer.deserialize(null, it) }
    }

    override fun getByIds(ids: Collection<String>): List<CleanedUser> {
        return rocksDB.multiGetAsList(ids.map { it.toByteArray() })
            .map { deserializer.deserialize(null, it) }
    }

    override fun put(id: String, cleanedUserBytes: ByteArray) {
        rocksDB.put(id.toByteArray(), cleanedUserBytes)
    }
}