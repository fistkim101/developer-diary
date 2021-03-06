package com.fistkim.coreweb.webclient

import ObjectMapperFactory
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration
import java.util.concurrent.TimeUnit


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
            exchangeFunction.exchange(request) // request 줄테니 response랑 교환하자! 라는 의미로 exchange 가 쓰였다고 보면 된다.
        }

        val connectionProvider = ConnectionProvider.builder(appName)
            .maxConnections(500) // 동시에 맺을 수 있는 connection 의 max
            .pendingAcquireTimeout(Duration.ofMillis(5000)) // 커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
            .pendingAcquireMaxCount(-1) // 커넥션을 얻기 위해서 대기하는 request 의 max count로 default 값은 maxConnection * 2이다. -1은 무한
            .maxLifeTime(Duration.ofMillis(8000L)) // 커넥션 풀에서 커넥션이 살아있을 수 있는 최대 수명 시간
            .maxIdleTime(Duration.ofMillis(8000L)) // 커넥션 풀에서 idle 상태의 커넥션을 유지하는 시간
            .build()

        // reactor netty(https://projectreactor.io/docs/netty/release/reference/index.html)
        // Reactor Netty offers backpressure-ready network engines for HTTP (including Websockets), TCP, and UDP.
        val httpClient = HttpClient.create(connectionProvider)

            // 요청을 보내고 응답을 받기 까지 허용할 최대 시간. 즉, 요청을 등록하고 모든 응답을 받고 요청이 remove 되기까지 걸리는 총 시간
            .responseTimeout(Duration.ofMillis(15000L))

            // Specifies whether GZip compression is enabled. (default : false)
            .compress(true)

            // keepAlive 허용여부(default : true)
            // keepAlive : 연결되어 있는 TCP 연결을 재사용하는 기능. 즉 Handshake 과정을 생략해서 자원을 아낀다.
            .keepAlive(true)

            // If the connection establishment attempt to the remote peer does not finish within the configured connect timeout (resolution: ms), the connection establishment attempt fails. Default: 30s.
            // "client 측에서 peer 에게 syn -> peer 가 다시 client 에게 syn-ack -> 클라이언트가 다시 peer에게 ack" =  connection establishment(https://www.catchpoint.com/blog/http-transaction-steps)
            // connection estabslishment 가 완료되는 데 까지 허용되는 시간. 이 시간 내에 안되면 Fail
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                10000
            )
            .doOnConnected {
                it.addHandler(WriteTimeoutHandler(15000, TimeUnit.MILLISECONDS))
                it.addHandler(ReadTimeoutHandler(15000, TimeUnit.MILLISECONDS))
            }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl("http://localhost:8080")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeaders { headers ->
                // header setting and basicAuth setting(headers.setBasicAuth())
            }
            .filter(loggingFilter)

            // exchange strategy setting(단건 전송별 코덱에서 핸들링 할 수 있는 메모리 사이즈, 디코딩 인코딩 시 사용할 objectMapper 및 전략 세팅. 인코딩 디코딩시 각각에 전략을 커스텀하려면 디코더, 인코더로 넣어주는 mapper 를 커스텀 해줘야한다)
            .exchangeStrategies(ExchangeStrategies.builder().codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)
                configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(ObjectMapperFactory.objectMapper))
                configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(ObjectMapperFactory.objectMapper))
            }.build())

    }
}


