package tui.meta.challenge.quotes.adaptors.api

import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import tui.meta.challenge.quotes.adaptors.persistence.QuoteRepository
import tui.meta.challenge.quotes.adaptors.persistence.entity.Quote

/**
 * End‑to‑end tests for the Quotes REST API.
 *
 * • Starts a full Spring Boot application context
 * • Uses a real MongoDB 7 Testcontainer
 * • Seeds and cleans data per test
 * • Calls the HTTP endpoints and verifies status + JSON payload
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuoteControllerIT {
    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var repo: QuoteRepository

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        private val mongo =
            MongoDBContainer(DockerImageName.parse("mongo:7")).apply {
                withReuse(true)
            }
    }

    @BeforeAll
    fun wireSpringToContainer() {
        System.setProperty("spring.data.mongodb.uri", mongo.replicaSetUrl)
    }

    @BeforeEach
    fun seed() {
        runBlocking {
            repo.deleteAll().block()

            val quotes =
                listOf(
                    Quote("Be yourself; everyone else is taken.", "Oscar Wilde", "inspiration"),
                    Quote("The only true wisdom is in knowing you know nothing.", "Socrates", "philosophy"),
                    Quote("Talk is cheap. Show me the code.", "Linus Torvalds", "programming"),
                )
            repo.saveAll(quotes).collectList().block()
        }
    }

    @Nested
    inner class GetById {
        @Test
        fun `GET by id returns 200 and body`() {
            runBlocking {
                val quote = repo.findAll().blockFirst()!!

                val response =
                    client
                        .get()
                        .uri("/quotes/${quote.id}")
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()

                response
                    .expectStatus()
                    .isOk
                    .expectBody()
                    .jsonPath("$.quoteAuthor")
                    .isEqualTo(quote.quoteAuthor)
                    .jsonPath("$.quoteText")
                    .isEqualTo(quote.quoteText)
            }
        }

        @Test
        fun `GET by id returns 404 for unknown id`() {
            val response = client.get().uri("/quotes/${ObjectId()}").exchange()

            response.expectStatus().isNotFound
        }

        @Test
        fun `GET by id returns 400 for invalid ObjectId format`() {
            val response = client.get().uri("/quotes/invalid-id").exchange()

            response.expectStatus().isBadRequest
        }
    }

    @Nested
    inner class GetByAuthor {
        @Test
        fun `GET by author is case insensitive`() {
            val author = "oscar wilde"

            val response =
                client
                    .get()
                    .uri {
                        it.path("/quotes").queryParam("author", author).build()
                    }.accept(MediaType.APPLICATION_JSON)
                    .exchange()

            response
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(1)
                .jsonPath("$[0].quoteAuthor")
                .isEqualTo("Oscar Wilde")
        }

        @Test
        fun `GET by author returns empty array when none match`() {
            val response =
                client
                    .get()
                    .uri {
                        it.path("/quotes").queryParam("author", "No One").build()
                    }.exchange()

            response
                .expectStatus()
                .isOk
                .expectBody()
                .json("[]")
        }

        @Test
        fun `GET by author returns 400 when query param is missing`() {
            val response = client.get().uri("/quotes?author=").exchange()

            response.expectStatus().isBadRequest
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `GET all returns every quote`() {
            runBlocking {
                val expectedCount = repo.count().block()!!

                val response = client.get().uri("/quotes").exchange()

                response
                    .expectStatus()
                    .isOk
                    .expectBody()
                    .jsonPath("$.length()")
                    .isEqualTo(expectedCount.toInt())
            }
        }

        @Test
        fun `GET all returns empty array when collection empty`() {
            runBlocking {
                repo.deleteAll().block()

                val response = client.get().uri("/quotes").exchange()

                response
                    .expectStatus()
                    .isOk
                    .expectBody()
                    .json("[]")
            }
        }
    }
}
