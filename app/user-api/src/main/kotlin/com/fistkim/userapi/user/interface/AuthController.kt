package com.fistkim.userapi.user.`interface`

import User
import org.springframework.web.bind.annotation.*

/**
 *
 * @author Leo
 */
@RestController
class AuthController {

    @PostMapping("/user")
    fun register(@RequestBody user: User): String {
        println("register in")
        return user.toString()
    }

    @GetMapping("/user")
    fun index(@RequestHeader(name = "Content-Location") location: String): String {
        println("index in")
        println("location : $location")
        return "success"
    }

    @GetMapping("/hello")
    fun hello(): String {
        return "success"
    }

}
