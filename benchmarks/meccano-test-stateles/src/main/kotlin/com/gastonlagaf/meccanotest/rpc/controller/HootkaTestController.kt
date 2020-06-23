package com.gastonlagaf.meccanotest.rpc.controller

import com.gastonlagaf.meccanotest.dto.UserDto
import com.gastonlagaf.meccano.annotation.Controller
import com.gastonlagaf.meccano.annotation.RequestMapping
import com.gastonlagaf.meccano.annotation.Stateless
import com.gastonlagaf.meccano.api.model.HttpMethod

@Controller
@RequestMapping(["/api/user"])
class HootkaTestController {

    @Stateless
    @RequestMapping(method = HttpMethod.GET)
    fun testWebflux(): UserDto = UserDto("Alice", "Johnson")

}