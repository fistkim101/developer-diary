package com.fistkim.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fistkim.common.support.ObjectMapperFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * @author Leo
 */
@Configuration
class AppConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapperFactory.objectMapper
    }

}
