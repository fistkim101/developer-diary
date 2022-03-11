package com.fistkim.coreweb.webclient

import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider

object WebClientBuilder {

    fun webClientBuilder(): WebClient.Builder {

        val logger = LoggerFactory.getLogger(WebClientBuilder::class.java)
        val appName = "developer-diary"

        // Represents a function that filters an exchange function. The filter is executed when a Subscriber subscribes to the Publisher returned by the WebClient.
        // webClient에 의해서 동작하는 filter 이다. function 이라 정의되어 있지만 request 를 예정된 response 와 exchange 해주는 ExchangeFunction을 filter 하는 역할을 하므로 사실상 filter 라고 봐도 될 것 같다.
        val loggingFilter = ExchangeFilterFunction { request, exchangeFunction ->
            logger.debug(appName)
            logger.debug("[${request.method()}][${request.url()}][${request.headers()}]")
            logger.debug("request body : {}", request.body())

            // ExchangeFunction : Represents a function that exchanges a request for a (delayed) ClientResponse. Can be used as an alternative to WebClient.
            // exchangeFunction 은 주어진 request를 예정된 response와 exchange 해준다. 'response 에 앞서서 파라미터로 받은 request를 선행 배치' 한다는 개념으로 'exchange'를 이해했다.
            exchangeFunction.exchange(request)
        }

        ConnectionProvider provider = ConnectionProvider.builder("ybs-pool")
            .maxConnections(500)
            .pendingAcquireTimeout(Duration.ofMillis(0))
            .pendingAcquireMaxCount(-1)
            .maxIdleTime(Duration.ofMillis(8000L))
            .maxLifeTime(Duration.ofMillis(8000L))
            .build();
        val httpClient = HttpClient.create(
            ConnectionProvider.builder(appName)
                .max
                .build()

        )




        return WebClient.builder()
            .baseUrl("http://localhost:8080") // @SpringBootTest 에서 특별히 포트를 설정해주지 않으면 8080을 기본값으로 테스트하게 된다
    }
}


