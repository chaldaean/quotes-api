package tui.meta.challenge.quotes.adaptors.persistence

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.kotlin.test.test
import reactor.test.StepVerifier
import tui.meta.challenge.quotes.adaptors.persistence.entity.Quote

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuoteRepositoryIT(
    @Autowired private val repo: QuoteRepository,
) {
    companion object {
        @Container
        @ServiceConnection
        private val mongo =
            MongoDBContainer(DockerImageName.parse("mongo:7")).apply {
                withReuse(true)
            }
    }

    @Test
    fun `save and findById works`() =
        runTest {
            // given
            val quote =
                Quote(
                    quoteText = "Talk is cheap. Show me the code.",
                    quoteAuthor = "Linus Torvalds",
                    quoteGenre = "programming",
                )
            val saved = repo.save(quote).awaitFirstOrNull()!!

            // when
            val byId = repo.findById(saved.id).awaitFirstOrNull()

            // then
            assertEquals(saved, byId)
        }

    @Test
    fun `findByQuoteAuthor returns all matching quotes`() =
        runTest {
            // given
            val quotes =
                listOf(
                    Quote(quoteText = "abc", quoteAuthor = "Ada", quoteGenre = "tech"),
                    Quote(quoteText = "def", quoteAuthor = "Ada", quoteGenre = "science"),
                    Quote(quoteText = "ghi", quoteAuthor = "Grace", quoteGenre = "tech"),
                )
            repo.saveAll(quotes).collectList().block()

            // when
            repo
                .findByQuoteAuthorIgnoreCase("Ada")
                .test()
                .assertNext { assertEquals("Ada", it.quoteAuthor) }
                .assertNext { assertEquals("Ada", it.quoteAuthor) }
                .expectNextCount(0)
                .verifyComplete()
        }

    @Test
    fun `findByQuoteAuthorIgnoreCase uses case-insensitive match`() {
        // arrange
        val quote =
            Quote(
                quoteText = "Innovation distinguishes between a leader and a follower.",
                quoteAuthor = "Steve Jobs",
                quoteGenre = "business",
            )
        repo.save(quote).block()

        // when / then
        StepVerifier
            .create(repo.findByQuoteAuthorIgnoreCase("sTeVe JoBs"))
            .expectNextCount(1)
            .verifyComplete()
    }
}
