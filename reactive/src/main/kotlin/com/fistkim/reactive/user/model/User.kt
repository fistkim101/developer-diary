package com.fistkim.reactive.user.model

data class User constructor(val name: String, val age: Int?, val roles: List<String>?) {
    init {

    }
}