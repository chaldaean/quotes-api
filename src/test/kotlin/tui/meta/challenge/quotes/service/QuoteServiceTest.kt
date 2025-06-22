package tui.meta.challenge.quotes.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tui.meta.challenge.quotes.adaptors.persistence.QuoteRepository
import tui.meta.challenge.quotes.adaptors.persistence.entity.Quote
import tui.meta.challenge.quotes.service.dto.QuoteDto

@Suppress("ReactiveStreamsUnusedPublisher")
class QuoteServiceTest {
    private val repo: QuoteRepository = mockk(relaxed = true)
    private val service = QuoteService(repo)

    @AfterEach
    fun tearDown() = clearAllMocks()

    @Nested
    inner class FindById {
        @Test
        fun `returns quote when id exists`() =
            runTest {
                // given
                val id = ObjectId().toString()
                val quote =
                    Quote(
                        quoteText = "Stay hungry, stay foolish.",
                        quoteAuthor = "Steve Jobs",
                        quoteGenre = "business",
                    ).also { it.id = id }
                every { repo.findById(id) } returns Mono.just(quote)

                // when
                val result = service.findById(id).firstOrNull()

                // then
                assertEquals(QuoteDto(quote), result)
                verify(exactly = 1) { repo.findById(id) }
            }

        @Test
        fun `returns empty flow when id does not exist`() =
            runTest {
                // given
                val id = ObjectId().toString()
                every { repo.findById(id) } returns Mono.empty()

                // when
                val result = service.findById(id).firstOrNull()

                // then
                assertNull(result)
                verify(exactly = 1) { repo.findById(id) }
            }
    }

    @Nested
    inner class FindByAuthorIgnoreCase {
        @Test
        fun `streams all quotes by author ignoring case`() =
            runTest {
                // given
                val quotes =
                    listOf(
                        Quote("a", "Ada", "tech").apply { id = ObjectId().toString() },
                        Quote("b", "Ada", "science").apply { id = ObjectId().toString() },
                    )
                every { repo.findByQuoteAuthorIgnoreCase("ada") } returns Flux.fromIterable(quotes)

                // when
                val result = service.findByAuthorIgnoreCase("ada").toList()

                // then
                assertEquals(2, result.size)
                assertTrue(result.all { it.quoteAuthor == "Ada" })
                verify(exactly = 1) { repo.findByQuoteAuthorIgnoreCase("ada") }
            }

        @Test
        fun `returns empty flow when author has no quotes`() =
            runTest {
                every { repo.findByQuoteAuthorIgnoreCase("unknown") } returns Flux.empty()

                val result = service.findByAuthorIgnoreCase("unknown").toList()

                assertTrue(result.isEmpty())
                verify(exactly = 1) { repo.findByQuoteAuthorIgnoreCase("unknown") }
            }
    }

    @Nested
    inner class FindAll {
        @Test
        fun `find all quotes`() =
            runTest {
                val quotes =
                    listOf(
                        Quote("quote‑1", "Author1", "genre").apply { id = ObjectId().toString() },
                        Quote("quote‑2", "Author2", "genre").apply { id = ObjectId().toString() },
                        Quote("quote‑3", "Author3", "genre").apply { id = ObjectId().toString() },
                        Quote("quote‑4", "Author4", "genre").apply { id = ObjectId().toString() },
                        Quote("quote‑5", "Author5", "genre").apply { id = ObjectId().toString() },
                    )
                every { repo.findAll() } returns Flux.fromIterable(quotes)

                val result = service.findAll().toList()

                assertEquals(5, result.size)
                verify(exactly = 1) { repo.findAll() }
            }

        @Test
        fun `returns empty flow when collection is empty`() =
            runTest {
                every { repo.findAll() } returns Flux.empty()

                val result = service.findAll().toList()

                assertTrue(result.isEmpty())
                verify(exactly = 1) { repo.findAll() }
            }
    }
}
