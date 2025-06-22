package tui.meta.challenge.quotes.adaptors.api

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.singleOrNull
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tui.meta.challenge.quotes.adaptors.api.exceptions.NotFoundErrorCodeException
import tui.meta.challenge.quotes.adaptors.persistence.entity.Quote
import tui.meta.challenge.quotes.service.QuoteService
import tui.meta.challenge.quotes.service.dto.QuoteDto

private val LOGGER = KotlinLogging.logger {}

/**
 * REST controller for accessing quote data via HTTP endpoints.
 *
 * <p>This controller exposes three endpoints:</p>
 * <ul>
 *   <li>GET /quotes/{id} — fetch a single quote by ID</li>
 *   <li>GET /quotes?author=X — fetch all quotes by a given author (case-insensitive)</li>
 *   <li>GET /quotes — fetch all quotes in the collection</li>
 * </ul>
 *
 * <p>Reactive results are returned as Kotlin [Flow], enabling efficient streaming
 * of large quote collections.</p>
 */
@RestController
@RequestMapping("/quotes")
class QuoteController(
    private val service: QuoteService,
) {
    /**
     * Retrieves a single quote by its MongoDB `_id`.
     *
     * @param id the unique identifier of the quote (MongoDB ObjectId).
     * @return the [Quote] document if found.
     * @throws NotFoundErrorCodeException if no quote with the specified ID exists.
     *
     * Example:
     * ```
     * GET /quotes/5eb17aaeb69dc744b4e72a4a
     * ```
     */
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getById(
        @PathVariable id: String,
    ): QuoteDto {
        LOGGER.trace { "Finding query by id $id" }
        return service.findById(id).singleOrNull() ?: run {
            LOGGER.debug { "Query not found for id $id" }
            throw NotFoundErrorCodeException(id)
        }
    }

    /**
     * Returns a case-insensitive list of all quotes by the specified author.
     *
     * @param author the author's name to search for (e.g., "bill gates").
     * @return a [Flow] streaming all matching quotes.
     *
     * Example:
     * ```
     * GET /quotes?author=Bill%20Gates
     * ```
     */
    @GetMapping(params = ["author"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByAuthor(
        @RequestParam author: String,
    ): Flow<QuoteDto> {
        LOGGER.trace { "Finding query by author $author" }
        return service.findByAuthorIgnoreCase(author)
    }

    /**
     * Find all quotes in the collection.
     *
     * <p>Intended for administrative tools or batch operations. Clients should
     * consume the [Flow] reactively to avoid memory pressure on large datasets
     * (e.g., 70k+ quotes).</p>
     *
     * Example:
     * ```
     * GET /quotes
     * ```
     *
     * @return a [Flow] of all [Quote] records.
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAll(): Flow<QuoteDto> {
        LOGGER.trace { "Finding all quotes" }
        return service.findAll()
    }
}
