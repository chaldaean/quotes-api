package tui.meta.challenge.quotes

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping(value = ["/hello"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun sayHello(): String {
        return "Hello with Spring Boot and Java!"
    }
}