package com.example.challengeCupon.challengeCupon.shared.model.exception

data class ErrorResponse(
    private val status: Int,
    private val error: String,
    private val message: String?
) {

    fun getStatus(): Int {
        return status
    }

    fun getError(): String {
        return error
    }

    fun getMessage(): String? {
        return message
    }

}