package pl.edu.agh.gem.external.controller

import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import pl.edu.agh.gem.error.SimpleErrorsHolder
import pl.edu.agh.gem.error.handleError
import pl.edu.agh.gem.exception.UserWithoutGroupAccessException
import pl.edu.agh.gem.internal.client.ExpenseManagerClientException
import pl.edu.agh.gem.internal.client.GroupManagerClientException
import pl.edu.agh.gem.internal.client.RetryableExpenseManagerClientException
import pl.edu.agh.gem.internal.client.RetryableGroupManagerClientException

@ControllerAdvice
@Order(LOWEST_PRECEDENCE)
class ApiExceptionHandler {

    @ExceptionHandler(UserWithoutGroupAccessException::class)
    fun handleUserWithoutGroupAccessException(
        exception: UserWithoutGroupAccessException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), FORBIDDEN)
    }

    @ExceptionHandler(GroupManagerClientException::class)
    fun handleGroupManagerClientException(exception: GroupManagerClientException): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(RetryableGroupManagerClientException::class)
    fun handleRetryableGroupManagerClientException(
        exception: RetryableGroupManagerClientException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(ExpenseManagerClientException::class)
    fun handleExpenseManagerClientException(exception: ExpenseManagerClientException): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(RetryableExpenseManagerClientException::class)
    fun handleRetryableExpenseManagerClientException(
        exception: RetryableExpenseManagerClientException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), INTERNAL_SERVER_ERROR)
    }
}
