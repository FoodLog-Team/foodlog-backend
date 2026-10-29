package team.foodlog.backend.exception.handler;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import team.foodlog.backend.exception.EmailAlreadyInUseException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyInUse(
            EmailAlreadyInUseException e, HttpServletRequest request
    ) {

        log.warn("Registration failed: method={}, uri={}, exception={}",
                request.getMethod(),
                request.getRequestURI(),
                e.getClass().getSimpleName(),
                e
        );

        return buildResponse(HttpStatus.CONFLICT, "This email is already in use");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            @NonNull HttpHeaders httpHeaders,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, List<String>> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(groupingBy(
                    FieldError::getField,
                    mapping(
                        fieldError -> Objects.requireNonNullElse(
                            fieldError.getDefaultMessage(),
                            "Validation failed"
                        ),
                        toList()
                    )
            ));

        log.warn("Request validation failed: errors={}", errors, e);

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(
            Exception e, HttpServletRequest request
    ) {

        log.error("Unhandled exception: type={}, method={}, uri={}",
                e.getClass().getSimpleName(),
                request.getMethod(),
                request.getRequestURI(),
                e
        );

        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal server error"
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                    status.value(),
                    message,
                    Instant.now()
            ));
    }

}
