package com.fistkim.reactive.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fistkim.reactive.support.ObjectMapperFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapperFactory.objectMapper
    }

}