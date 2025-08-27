package com.example.challengeCupon.challengeCupon.shared

import com.example.challengeCupon.challengeCupon.adapter.exception.BadRequestException
import com.example.challengeCupon.challengeCupon.adapter.exception.NotFoundException
import com.example.challengeCupon.challengeCupon.adapter.exception.UnprocessableException
import com.example.challengeCupon.challengeCupon.shared.model.exception.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException) =
        ResponseEntity(
            ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.reasonPhrase, ex.message),
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException) =
        ResponseEntity(
            ErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.reasonPhrase, ex.message),
            HttpStatus.NOT_FOUND
        )

    @ExceptionHandler(UnprocessableException::class)
    fun handleUnprocessable(ex: UnprocessableException) =
        ResponseEntity(
            ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY.reasonPhrase, ex.message),
            HttpStatus.UNPROCESSABLE_ENTITY
        )

}