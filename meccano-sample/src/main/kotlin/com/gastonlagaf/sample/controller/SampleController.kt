package com.gastonlagaf.sample.controller

import com.gastonlagaf.meccano.annotation.*
import com.gastonlagaf.meccano.api.SecurityProvider
import com.gastonlagaf.meccano.api.WrappedHttpRequest
import com.gastonlagaf.meccano.api.WrappedHttpResponse
import com.gastonlagaf.meccano.api.model.HttpResponseStatus
import com.gastonlagaf.meccano.api.model.MimeType
import com.gastonlagaf.meccano.api.model.SimpleAuthenticationDetails
import com.gastonlagaf.meccano.api.support.HttpMethod
import com.gastonlagaf.meccano.api.support.InMemoryFile
import com.gastonlagaf.sample.domain.AuthRequest
import com.gastonlagaf.sample.domain.MultiObject
import com.gastonlagaf.sample.domain.User
import com.gastonlagaf.sample.domain.UserDto
import javax.validation.Valid

@Controller
class SampleController(
    private val securityProvider: SecurityProvider<SimpleAuthenticationDetails>
) {

    @Stateless
    @RequestMapping(method = HttpMethod.GET, value = ["/status"], produces = MimeType.TEXT_PLAIN)
    fun status(): String {
        return "Hello World"
    }

    @RequestMapping(method = HttpMethod.GET, value = ["/redirect"])
    fun redirect(): String {
        return "redirect:/status"
    }

    @Stateless
    @RequestMapping(method = HttpMethod.GET, value = ["/api/greet"], produces = MimeType.TEXT_PLAIN)
    fun greet(@RequestParam name: String?): String {
        return "Greetings, $name!!!"
    }

    @Stateless
    @ResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
    @RequestMapping(method = HttpMethod.POST, value = ["/api/mutate/{firstName}"])
    fun mutate(@RequestBody request: UserDto, @PathVariable firstName: String): UserDto {
        return UserDto(firstName, request.lastName, request.age)
    }

    @Stateless
    @RequestMapping(method = HttpMethod.GET, value = ["/api/user"])
    fun user(): UserDto = UserDto("Alice", "Johnson", 22)

    @Stateless
    @RequestMapping(method = HttpMethod.POST, value = ["/api/user"])
    fun addUser(@ModelAttribute request: UserDto): UserDto {
        return UserDto(request.firstName, request.lastName, request.age)
    }

    @Stateless
    @RequestMapping(method = HttpMethod.POST, value = ["/api/image"], produces = MimeType.TEXT_PLAIN)
    fun addImage(@MultipartFile(acceptExtensions = ["png"]) file: InMemoryFile): String {
        return "File \"${file.name}\" upload success!"
    }

    @Stateless
    @RequestMapping(method = HttpMethod.POST, value = ["/api/describe-image"], produces = MimeType.TEXT_PLAIN)
    fun addImageWithDescription(@MultipartObject(acceptExtensions = ["png"]) multiObject: MultiObject): String {
        return "File \"${multiObject.file.name}\" upload success!\nDescription: \"${multiObject.description}\""
    }

    @Stateless
    @RequestMapping(method = HttpMethod.GET, value = ["/api/fail"])
    fun fail() {
        throw IllegalArgumentException()
    }

    @PreAuthorize("nobody()")
    @RequestMapping(method = HttpMethod.GET, value = ["/api/secure"])
    fun secure(): UserDto {
        return UserDto("John", "Doe", 22)
    }

    @RequestMapping(method = HttpMethod.POST, value = ["/login"])
    fun login(@ModelAttribute @Valid authRequest: AuthRequest, response: WrappedHttpResponse,
              request: WrappedHttpRequest): String {
        val authDetails = SimpleAuthenticationDetails(
            authRequest.username!!,
            authRequest.password!!,
            request,
            response,
            authRequest.rememberMe!!
        )
        securityProvider.authenticate(authDetails)
        return "Login success!"
    }

    @RequestMapping(method = HttpMethod.GET, value = ["/api/principal/secure"], produces = MimeType.TEXT_PLAIN)
    fun principalSecure(@Principal user: User?): String {
        return if (null == user) {
            "Please, login."
        } else {
            "Welcome, ${user.getUsername()}!"
        }
    }

}