package com.example.challengeCupon.challengeCupon.shared.model

enum class ErrorDescription(val value: String) {
    UNEXPECTED_ERROR("An unexpected error occurred: "),
    UNHANDLED("An unexpected error occurred."),
    BAD_REQUEST("Bad Request."),
    FORBIDDEN("Access to the requested resource is forbidden."),
    NOT_FOUND("Item not found with id: "),
}