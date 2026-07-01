package springdev.ecomv0.monolithicmanager.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import springdev.ecomv0.monolithicmanager.exception.ApiError;
import springdev.ecomv0.monolithicmanager.exception.BusinessException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle BusinessException
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        ApiError apiError = buildApiError(ex.getMessage(), ex.getStatus(), request.getRequestURI(), List.of());
        return ResponseEntity.status(ex.getStatus()).body(apiError);
    }

    // Handle other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
        ApiError apiError = buildApiError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(),
                List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    private ApiError buildApiError(String message, HttpStatus status, String path, List<String> details) {
        ApiError apiError = ApiError.builder()
                .timestamp(java.time.Instant.now())
                .message(message)
                .status(status.value())
                .error(status.getReasonPhrase())
                .path(path)
                .details(details)
                .build();

        return apiError;
    }

}