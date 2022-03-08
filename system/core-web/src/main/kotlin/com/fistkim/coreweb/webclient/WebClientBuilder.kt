package com.fistkim.coreweb.webclient

import org.springframework.web.reactive.function.client.WebClient

object WebClientBuilder {

    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
            .baseUrl("http://localhost:8080")
    }

}
