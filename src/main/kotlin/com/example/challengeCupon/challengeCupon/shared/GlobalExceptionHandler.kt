import com.example.challengeCupon.challengeCupon.adapter.exception.NotAvailableException
import com.example.challengeCupon.challengeCupon.shared.model.exception.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(NotAvailableException::class)
    fun handleNotAvailable(
        ex: NotAvailableException
    ): ResponseEntity<ErrorResponse> {
        val status = when {
            ex.message?.contains("not found", true) == true -> HttpStatus.NOT_FOUND
            ex.message?.contains("access denied", true) == true -> HttpStatus.FORBIDDEN
            else -> HttpStatus.BAD_GATEWAY
        }
        val body = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
        )
        return ResponseEntity(body, status)
    }

}