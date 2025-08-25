package com.example.challengeCupon.challengeCupon.shared.model.exception

import java.time.LocalDateTime

data class ErrorResponse(
    private val timestamp: LocalDateTime = LocalDateTime.now(),
    private val status: Int,
    private val error: String,
    private val message: String?
) {
}