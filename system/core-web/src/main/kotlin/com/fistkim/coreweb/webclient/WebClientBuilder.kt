package com.fistkim.coreweb.webclient

import org.springframework.web.reactive.function.client.WebClient

object WebClientBuilder {

    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
            .baseUrl("http://localhost:8080") // @SpringBootTest 에서 특별히 포트를 설정해주지 않으면 8080을 기본값으로 테스트하게 된다
    }

}
