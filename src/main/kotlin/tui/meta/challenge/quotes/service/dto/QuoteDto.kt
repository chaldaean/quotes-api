package tui.meta.challenge.quotes.service.dto

import tui.meta.challenge.quotes.adaptors.persistence.entity.Quote

data class QuoteDto(
    val id: String,
    val quoteText: String,
    val quoteAuthor: String,
    val quoteGenre: String,
) {
    constructor(quote: Quote) : this(
        id = quote.id,
        quoteText = quote.quoteText,
        quoteAuthor = quote.quoteAuthor,
        quoteGenre = quote.quoteGenre,
    )
}
