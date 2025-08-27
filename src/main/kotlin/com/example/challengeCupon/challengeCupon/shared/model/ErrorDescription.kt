package com.example.challengeCupon.challengeCupon.shared.model

enum class ErrorDescription(val value: String) {
    UNHANDLED("An unexpected error occurred."),
    WRONG_AMOUNT("Amount must have at most two decimal places"),
    DUPLICATE_ITEMS("Duplicate items are not allowed: "),
    EMPTY_ITEMS("Items list must not be empty."),
    NON_POSITIVE_AMOUNT("Amount must be greater than 0."),
    NOT_FOUND("Not found items with ids: "),
    TOO_MANY_ITEMS("Too many items (max 100)"),
    AMOUNT_TOO_LARGE("Amount too large"),
}