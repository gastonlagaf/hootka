package com.gastonlagaf.sample.config

import com.gastonlagaf.meccano.api.SecurityExpressionInitializer
import com.gastonlagaf.meccano.api.UserDetails
import com.gastonlagaf.meccano.api.UserDetailsService
import com.gastonlagaf.meccano.api.internal.security.RootSecurityExpressions
import com.gastonlagaf.meccano.helper.DefaultExceptionControllerAdvice
import com.gastonlagaf.sample.domain.Role
import com.gastonlagaf.sample.domain.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun securityExpressionInitializer(): SecurityExpressionInitializer = object : SecurityExpressionInitializer {
        override fun createSecurityExpressions(principal: UserDetails?): RootSecurityExpressions {
            return CustomSecurityExpression()
        }
    }

    @Bean
    fun defaultExceptionControllerAdvice(): DefaultExceptionControllerAdvice {
        return DefaultExceptionControllerAdvice()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return object : UserDetailsService {
            override fun findUserDetailsByUsername(value: String): UserDetails? {
                val email = "some@gmail.com"
                return if (value == email) {
                    User(
                        email,
                        "\$2a\$10\$P.mTV9OcadADM0PhFzaMueexn3lrUV.D01CnmTi4uxdGMdrISN8wK",
                        setOf(Role("ADMIN")),
                        true
                    ) // password is 'password'
                } else null
            }
        }
    }

}

class CustomSecurityExpression : RootSecurityExpressions() {

    fun nobody(): Boolean {
        return false
    }

}