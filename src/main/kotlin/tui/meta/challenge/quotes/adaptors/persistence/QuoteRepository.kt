package tui.meta.challenge.quotes.adaptors.persistence

import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import tui.meta.challenge.quotes.adaptors.persistence.entity.Quote

/**
 * Reactive repository for `Quote` documents.
 */
interface QuoteRepository : ReactiveCrudRepository<Quote, String> {
    /**
     * Case‑*insensitive* search by author.
     *
     * The query uses an equality match (`{'quoteAuthor': ?0}`) together with the same
     * collation specified on the index, allowing MongoDB to satisfy the query via
     * **`author_idx_ci`** instead of performing a collection scan.
     *
     * @param author Author name in any casing.
     * @return Stream of matching quotes.
     */
    @Query(
        value = "{ 'quoteAuthor' : ?0 }",
        collation = "{ locale: 'en', strength: 2 }", // must match index collation
    )
    fun findByQuoteAuthorIgnoreCase(author: String): Flux<Quote>
}
