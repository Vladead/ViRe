package com.vire.virebackend.controller.advice;

import com.vire.virebackend.problem.ProblemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order
public class FallbackExceptionHandler {

    private final ProblemFactory problemFactory;

    // 500 others
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAny(Exception exception) {
        var incidentId = UUID.randomUUID();
        log.error("Unhandled exception, incidentId={}", incidentId, exception);

        return problemFactory.internal(incidentId);
    }
}
