package com.fistkim.reactive.support

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

object ObjectMapperFactory {

    val objectMapper = ObjectMapper()

    init {

        // 정의되지 않은 필드(매핑되지 않는 필드)가 들어오면 매핑 에러를 발생시킬지 말지에 대한 여부
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        objectMapper.registerModule(
            KotlinModule.Builder()

                // 매핑되지 않는 필드(미존재 필드)는 null 로 취급할지 여부, 필드명이 일치하는 것이 없으면 null을 대입하게 된다.
                // ***이때 필드가 nullable 하지 않으면 에러가 발생
                .configure(KotlinFeature.NullIsSameAsDefault, true)
                .build()
        )
    }

}