package com.vantara.platform.shared.interfaces.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Interface-layer exception handler that translates domain and validation
 * errors into HTTP problem responses.
 *
 * <p>Handles:</p>
 * <ul>
 *     <li>{@link IllegalArgumentException}: domain invariant violations and
 *     input validation.</li>
 *     <li>{@link MethodArgumentNotValidException}: Jakarta Bean Validation
 *     failures on request resources.</li>
 * </ul>
 *
 * <p>Error messages are localized per request locale.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Handles Jakarta Bean Validation failures on request bodies.
     *
     * @param exception the validation exception
     * @param locale    request locale
     * @return HTTP 400 problem response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleException(MethodArgumentNotValidException exception, Locale locale) {
        String prefix = messageSource.getMessage("errors.found", null, locale);
        String fields = exception.getFieldErrors().stream()
                .map(fieldError -> messageSource.getMessage(fieldError, locale))
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", fields);
        return ErrorResponse.create(
                exception,
                HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()),
                prefix + " " + fields
        );
    }

    /**
     * Handles domain invariant violations and invalid input.
     *
     * @param exception the illegal argument exception
     * @param locale    request locale
     * @return HTTP 400 problem response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleException(IllegalArgumentException exception, Locale locale) {
        String messageKey = exception.getMessage() != null ? exception.getMessage() : "errors.found";
        String message = Objects.requireNonNullElse(
                messageSource.getMessage(messageKey, null, messageKey, locale),
                messageKey);
        log.warn("Illegal argument: {}", message);
        return ErrorResponse.create(
                exception,
                HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()),
                message);
    }
}
