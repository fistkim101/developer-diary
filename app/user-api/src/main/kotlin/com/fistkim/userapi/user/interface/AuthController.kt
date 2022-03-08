package com.fistkim.userapi.user.`interface`

import User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author Leo
 */
@RestController
class AuthController {

    @PostMapping("/user")
    fun register(@RequestBody user: User): String {
        return user.toString()
    }

}
