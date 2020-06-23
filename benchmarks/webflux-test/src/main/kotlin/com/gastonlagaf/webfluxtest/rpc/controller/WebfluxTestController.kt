package com.gastonlagaf.webfluxtest.rpc.controller

import com.gastonlagaf.webfluxtest.dto.UserDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user")
class WebfluxTestController {

    @GetMapping
    fun testWebflux(): UserDto = UserDto("Alice", "Johnson")

}