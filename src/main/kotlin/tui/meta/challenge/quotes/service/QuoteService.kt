package tui.meta.challenge.quotes.service

import kotlinx.coroutines.flow.Flow
import tui.meta.challenge.quotes.service.dto.QuoteDto

/**
 * Contract for the quote service exposing reactive operations.
 *
 * Designed for testing, mocking, and decoupling controllers from implementations.
 */
interface QuoteService {
    /**
     * Retrieves a single quote by its ID.
     */
    fun findById(id: String): Flow<QuoteDto>

    /**
     * Retrieves all quotes by a given author (case-insensitive).
     */
    fun findByAuthorIgnoreCase(author: String): Flow<QuoteDto>

    /**
     * Retrieves all quotes in the collection.
     */
    fun findAll(): Flow<QuoteDto>
}
