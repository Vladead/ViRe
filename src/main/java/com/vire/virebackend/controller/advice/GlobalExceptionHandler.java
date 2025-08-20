package com.vire.virebackend.controller.advice;

import com.vire.virebackend.problem.ProblemFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ProblemFactory problemFactory;

    // 400 @Valid body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleInvalidBody(MethodArgumentNotValidException exception) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        for (FieldError fe : exception.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(fe.getField(), i -> new ArrayList<>())
                    .add(Objects.toString(fe.getDefaultMessage(), "Invalid value"));
        }
        return problemFactory.validation(errors);
    }

    // 400 @Validated params/path + JSON parsing
    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public ProblemDetail handleBadRequest(Exception exception) {
        if (exception instanceof ConstraintViolationException cve) {
            Map<String, List<String>> errors = new LinkedHashMap<>();
            for (ConstraintViolation<?> v : cve.getConstraintViolations()) {
                var field = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "param";
                errors.computeIfAbsent(field, i -> new ArrayList<>()).add(v.getMessage());
            }

            return problemFactory.validation(errors);
        }

        return problemFactory.badRequest("Malformed or invalid request");
    }

    // 404 not found
    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    public ProblemDetail handleNotFound(RuntimeException exception) {
        return problemFactory.notFound("Resource not found");
    }

    // 409 from @Version
    @ExceptionHandler(OptimisticLockException.class)
    public ProblemDetail handleConflict(OptimisticLockException exception) {
        return problemFactory.conflict("Resource modified concurrently");
    }

    // 500 others
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAny(Exception exception) {
        var incidentId = UUID.randomUUID();
        log.error("Unhandled exception, incidentId={}", incidentId, exception);

        return problemFactory.internal(incidentId);
    }
}
