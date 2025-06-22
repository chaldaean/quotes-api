package tui.meta.challenge.quotes.adaptors.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.coEvery
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import tui.meta.challenge.quotes.service.QuoteService
import tui.meta.challenge.quotes.service.dto.QuoteDto

@WebFluxTest(controllers = [QuoteController::class])
@AutoConfigureWebTestClient
class QuoteControllerTest {
    @Autowired
    lateinit var client: WebTestClient

    @MockkBean
    lateinit var service: QuoteService

    @AfterEach
    fun tearDown() = clearAllMocks()

    // ---------------------------------------------------------------------
    // GET /quotes/{id}
    // ---------------------------------------------------------------------
    @Nested
    inner class GetById {
        @Test
        fun `returns 200 and quote when id exists`() {
            // given
            val id = ObjectId().toString()
            val quote = QuoteDto(id, "Do. Or do not.", "Yoda", "wisdom")
            coEvery { service.findById(id) } returns flowOf(quote)

            // when
            val response =
                client
                    .get()
                    .uri("/quotes/$id")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()

            // then
            response
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.quoteAuthor")
                .isEqualTo("Yoda")
                .jsonPath("$.quoteText")
                .isEqualTo("Do. Or do not.")
        }

        @Test
        fun `returns 404 when id not found`() {
            // given
            val id = ObjectId().toString()
            coEvery { service.findById(id) } returns emptyFlow()

            // when
            val response = client.get().uri("/quotes/$id").exchange()

            // then
            response
                .expectStatus()
                .isNotFound
                .expectBody()
                .jsonPath("$.error")
                .isEqualTo("Not Found")
        }
    }

    // ---------------------------------------------------------------------
    // GET /quotes?author=
    // ---------------------------------------------------------------------
    @Nested
    inner class GetByAuthor {
        @Test
        fun `returns quotes for author ignoring case`() {
            // given
            val quotes =
                listOf(
                    QuoteDto(ObjectId().toString(), "abc", "Ada", "tech"),
                    QuoteDto(ObjectId().toString(), "def", "Ada", "science"),
                )
            coEvery { service.findByAuthorIgnoreCase("Ada") } returns
                flowOf(*quotes.toTypedArray())

            // when
            val response =
                client
                    .get()
                    .uri { it.path("/quotes").queryParam("author", "Ada").build() }
                    .exchange()

            // then
            response
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(2)
        }

        @Test
        fun `returns empty array when author has no quotes`() {
            // given
            coEvery { service.findByAuthorIgnoreCase("Unknown") } returns emptyFlow()

            // when
            val response =
                client
                    .get()
                    .uri { it.path("/quotes").queryParam("author", "Unknown").build() }
                    .exchange()

            // then
            response
                .expectStatus()
                .isOk
                .expectBody()
                .json("[]")
        }
    }

    // ---------------------------------------------------------------------
    // GET /quotes
    // ---------------------------------------------------------------------
    @Nested
    inner class GetAll {
        @Test
        fun `streams all quotes`() {
            // given
            val quotes =
                listOf(
                    QuoteDto(ObjectId().toString(), "q1", "A1", "g1"),
                    QuoteDto(ObjectId().toString(), "q2", "A2", "g2"),
                )
            coEvery { service.findAll() } returns flowOf(*quotes.toTypedArray())

            // when
            val response = client.get().uri("/quotes").exchange()

            // then
            response
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(2)
        }

        @Test
        fun `returns empty array when database is empty`() {
            // given
            coEvery { service.findAll() } returns emptyFlow()

            // when
            val response = client.get().uri("/quotes").exchange()

            // then
            response
                .expectStatus()
                .isOk
                .expectBody()
                .json("[]")
        }
    }
}
