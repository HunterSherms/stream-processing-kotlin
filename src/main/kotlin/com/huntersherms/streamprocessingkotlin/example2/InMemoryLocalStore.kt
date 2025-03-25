package com.huntersherms.streamprocessingkotlin.example2

import com.github.benmanes.caffeine.cache.Caffeine
import com.huntersherms.streamprocessingkotlin.shared.CleanedUser
import com.huntersherms.streamprocessingkotlin.shared.CleanedUserSerde
import com.huntersherms.streamprocessingkotlin.shared.LocalCleanedUserStore
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Represents your in-memory storage.
 */
@Service
@Profile("example2")
class InMemoryLocalStore: LocalCleanedUserStore {

    private val localCache = Caffeine.newBuilder()
        .build<String, CleanedUser>()
    private val deserializer = CleanedUserSerde().deserializer()

    override fun getById(id: String): CleanedUser? {
        return localCache.getIfPresent(id)
    }

    override fun getByIds(ids: Collection<String>): List<CleanedUser> {
        return localCache.getAllPresent(ids).values.toList()
    }

    override fun put(id: String, cleanedUserBytes: ByteArray) {
        localCache.put(id, deserializer.deserialize(null, cleanedUserBytes))
    }
}