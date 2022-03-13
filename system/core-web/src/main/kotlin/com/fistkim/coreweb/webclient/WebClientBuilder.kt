package com.fistkim.coreweb.webclient

import io.netty.channel.ChannelOption
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

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
            // 여기서 request와 ClientResponse를 exchange 한다는 개념이 이해가 잘 가지 않았는데, 코드를 까보면(spring-web-5.3.16-sources.jar!/org/springframework/http/client/reactive/ReactorClientHttpConnector.java:113)
            // 에서 실제로 요청할 request를 통해서 response를 가져오는 것을 알 수 있다. 이게 곧 request 와 response 를 교환한다는 것이다. exchange 자체가 Mono<ClientResponse> 를 output 으로 하는 function 이라는 것을 생각하면 이해가 더 쉽다.
            // 즉, request 를 보내고 response 를 받는 일반적인 과정을 '교환'이라는 의미로 해석하면 이해가 쉽다. 이 '교환' 절차 전에 request에 특별한 가공을 한다던가, 로깅과 같은 행위를 할 수 있기 때문에 filter라고 말할 수 있다.
            exchangeFunction.exchange(request)
        }

        val connectionProvider = ConnectionProvider.builder(appName)
            .maxConnections(500) // 동시에 맺을 수 있는 connection 의 max
            .pendingAcquireTimeout(Duration.ofMillis(5000)) // 커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
            .pendingAcquireMaxCount(-1) // 커넥션을 얻기 위해서 대기하는 request 의 max count로 default 값은 maxConnection * 2이다. -1은 무한
            .maxLifeTime(Duration.ofMillis(8000L)) // 커넥션 풀에서 커넥션이 살아있을 수 있는 최대 수명 시간
            .maxIdleTime(Duration.ofMillis(8000L)) // 커넥션 풀에서 idle 상태의 커넥션을 유지하는 시간
            .build();

        val httpClient = HttpClient.create(connectionProvider)

            // 요청을 보내고 응답을 받기 까지 허용할 최대 시간. 즉, 요청을 등록하고 모든 응답을 받고 요청이 remove 되기까지 걸리는 총 시간
            .responseTimeout(Duration.ofMillis(15000L))

            // Specifies whether GZip compression is enabled. (default : false)
            .compress(true)

            // keepAlive 허용여부(default : true)
            // keepAlive : 연결되어 있는 TCP 연결을 재사용하는 기능. 즉 Handshake 과정을 생략해서 자원을 아낀다.
            .keepAlive(true)

        // https://projectreactor.io/docs/netty/release/reference/index.html
        // 문서 보고 추가적으로 필요한 부분들 정리 및 활용

        return WebClient.builder()
            .baseUrl("http://localhost:8080") // @SpringBootTest 에서 특별히 포트를 설정해주지 않으면 8080을 기본값으로 테스트하게 된다
    }
}


