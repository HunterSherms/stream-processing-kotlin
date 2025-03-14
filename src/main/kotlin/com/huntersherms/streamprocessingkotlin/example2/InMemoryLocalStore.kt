package com.huntersherms.streamprocessingkotlin.example2

import com.github.benmanes.caffeine.cache.Caffeine
import com.huntersherms.streamprocessingkotlin.shared.CleanedUser
import com.huntersherms.streamprocessingkotlin.shared.LocalCleanedUserStore
import org.springframework.stereotype.Service

/**
 * Represents your in-memory storage.
 */
@Service("inMemoryLocalStore")
class InMemoryLocalStore: LocalCleanedUserStore {

    private val localCache = Caffeine.newBuilder()
        .build<String, CleanedUser>()

    override fun getById(id: String): CleanedUser? {
        return localCache.getIfPresent(id)
    }

    override fun getByIds(ids: Collection<String>): List<CleanedUser> {
        return localCache.getAllPresent(ids).values.toList()
    }

    override fun put(id: String, cleanedUser: CleanedUser) {
        localCache.put(id, cleanedUser)
    }
}