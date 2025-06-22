package tui.meta.challenge.quotes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [MongoAutoConfiguration::class])
class QuotesApplication

fun main(args: Array<String>) {
    runApplication<QuotesApplication>(*args)
}
