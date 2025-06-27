package tui.meta.challenge.quotes.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Service
import tui.meta.challenge.quotes.adaptors.persistence.QuoteRepository
import tui.meta.challenge.quotes.service.dto.QuoteDto

private val LOGGER = KotlinLogging.logger {}

/**
 * Application‑level façade that exposes reactive, non‑blocking operations
 * for working with {@link Quote} documents.
 *
 * <p>Why have a separate <em>service</em> layer when the repository could be
 * injected directly into the controller?</p>
 * <ul>
 *   <li>Encapsulates business rules (e.g.&nbsp;validation, enrichment) in one place.</li>
 *   <li>Keeps controllers slim and HTTP‑agnostic.</li>
 *   <li>Makes unit testing easier—service methods can be tested without
 *       spinning up WebFlux.</li>
 * </ul>
 *
 * The class is a Spring singleton declared with {@code @Service}; constructor
 * injection is idiomatic in Kotlin and requires no additional annotations.
 */
@Service
class QuoteServiceImpl(
    private val repo: QuoteRepository,
) : QuoteService {
    /**
     * Retrieves a single quote by its MongoDB identifier.
     *
     * @param id the {@link ObjectId} of the quote to fetch.
     * @return a cold [Flow] that will emit:
     *   <ul>
     *     <li>a single {@link QuoteDto} if the document exists, or</li>
     *     <li>completes empty if no document is found.</li>
     *   </ul>
     */
    override fun findById(id: String): Flow<QuoteDto> =
        repo
            .findById(id)
            .map {
                LOGGER.debug { "Found query $it" }
                QuoteDto(it)
            }.asFlow()

    /**
     * Case‑insensitive search for all quotes written by the given author.
     *
     * <p>Backed by the compound index {@code author_idx_ci} (strength 2
     * collation), so the query remains efficient regardless of input casing.</p>
     *
     * @param author the author name in any combination of upper/lower case.
     * @return a [Flow] streaming every matching {@link QuoteDto}.
     */
    override fun findByAuthorIgnoreCase(author: String): Flow<QuoteDto> =
        repo
            .findByQuoteAuthorIgnoreCase(author)
            .map {
                LOGGER.debug { "Found query $it" }
                QuoteDto(it)
            }.asFlow()

    /**
     * Streams <em>all</em> quotes in the collection.
     *
     * <p>Intended primarily for administrative or batch use‑cases; clients
     * should consume the flow reactively to avoid loading all documents
     * into memory at once.</p>
     *
     * @return a [Flow] that emits all {@link QuoteDto} documents.
     */
    override fun findAll(): Flow<QuoteDto> =
        repo
            .findAll()
            .map {
                LOGGER.debug { "Found query $it" }
                QuoteDto(it)
            }.asFlow()
}
