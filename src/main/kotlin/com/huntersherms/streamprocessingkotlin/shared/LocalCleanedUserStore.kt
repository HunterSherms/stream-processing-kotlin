package com.huntersherms.streamprocessingkotlin.shared

interface LocalCleanedUserStore {

    fun getById(id: String): CleanedUser?

    fun getByIds(ids: Collection<String>): List<CleanedUser>

    fun put(id: String, cleanedUser: CleanedUser)
}