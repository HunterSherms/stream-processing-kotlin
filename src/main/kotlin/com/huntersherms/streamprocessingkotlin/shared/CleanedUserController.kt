package com.huntersherms.streamprocessingkotlin.shared;

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class CleanedUserController(private val store: LocalCleanedUserStore) {

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): CleanedUser? {
        return store.getById(id)
    }

    @GetMapping
    fun getUsersByIds(@RequestParam("ids") ids: String): List<CleanedUser> {
        val users = store.getByIds(ids.split(","))
        return users
    }
}