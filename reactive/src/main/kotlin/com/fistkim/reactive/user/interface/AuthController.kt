package com.fistkim.reactive.user.`interface`

import com.fistkim.reactive.user.model.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController {

    @PostMapping("/user")
    fun register(@RequestBody user: User): String {
        return user.toString()
    }

}