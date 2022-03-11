package com.fistkim.userapi.webclient

import User
import com.fistkim.coreweb.webclient.WebClientBuilder
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.BodyInserters
import java.util.*

/**
 *
 * @author Leo
 */
@SpringBootTest
class WebClientTest {

    val userApiWebClient = WebClientBuilder.webClientBuilder().build()

    @Test
    fun webClientTestIndex() {
        val result = userApiWebClient.get()
            .uri("/user")
            .header("Content-Location", "KR")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        Assertions.assertThat(result).isEqualTo("success")
    }


    @Test
    fun webClientTestRegister() {
        val result = userApiWebClient.post()
            .uri("/user")
            .body(BodyInserters.fromValue(User(name = "kim", age = 50, roles = Collections.emptyList())))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

    }

}
