package tui.meta.challenge.quotes.adaptors.api.dto

import tui.meta.challenge.quotes.service.dto.QuoteDto

data class QuoteResponse(
    val id: String,
    val quoteText: String,
    val quoteAuthor: String,
    val quoteGenre: String,
) {
    constructor(quote: QuoteDto) : this(
        id = quote.id,
        quoteText = quote.quoteText,
        quoteAuthor = quote.quoteAuthor,
        quoteGenre = quote.quoteGenre,
    )
}
