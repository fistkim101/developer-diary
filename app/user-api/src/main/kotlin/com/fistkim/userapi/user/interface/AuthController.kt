package com.fistkim.userapi.user.`interface`

import User
import com.fistkim.userapi.config.AppConfig
import org.springframework.web.bind.annotation.*

/**
 *
 * @author Leo
 */
@RestController
class AuthController(val appConfig: AppConfig) {

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
        appConfig.profileTest()
        return "success"
    }

}
