package com.fistkim.userapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

/**
 *
 * @author Leo
 */
@Profile("batch")
@Configuration
class AppConfig(val environment: Environment) {

    fun profileTest(){
        println(environment.acceptsProfiles(Profiles.of("prod")))
    }

}
