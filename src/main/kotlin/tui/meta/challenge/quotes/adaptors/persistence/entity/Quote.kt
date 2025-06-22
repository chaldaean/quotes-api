package tui.meta.challenge.quotes.adaptors.persistence.entity

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId

/**
 * Represents a quote stored in the MongoDB collection "quotes".
 *
 * > **Index note**
 * > The `author_idx_ci` index is declared with an *English collation* at strength 2, which
 * > means comparisons are **case‑insensitive** but still accent‑sensitive.
 * > With this index in place, equality matches on `quoteAuthor` (e.g. `"bill gates"`) can
 * > use the index efficiently instead of falling back to a regex scan.
 */
@CompoundIndexes(
    CompoundIndex(
        name = "author_idx_ci",
        def = "{ 'quoteAuthor' : 1 }",
        collation = "{ locale: 'en', strength: 2 }", // case‑insensitive collation
    ),
)
@Document(collection = "quotes")
data class Quote(
    val quoteText: String,
    val quoteAuthor: String,
    val quoteGenre: String,
) {
    /** MongoDB‑generated unique identifier. */
    @MongoId(FieldType.STRING)
    lateinit var id: String
}
