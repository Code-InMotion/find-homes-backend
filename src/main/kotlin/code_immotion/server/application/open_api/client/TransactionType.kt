package code_immotion.server.application.open_api.client

enum class TransactionType(val value: String) {
    SALE("Trade"),
    RENT("Rent")
}