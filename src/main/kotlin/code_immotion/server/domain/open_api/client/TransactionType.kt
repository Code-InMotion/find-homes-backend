package code_immotion.server.domain.open_api.client

enum class TransactionType(val value: String) {
    SALE("Trade"),
    RENT("Rent")
}