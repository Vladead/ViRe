package com.vire.virebackend.controller.advice;

import com.vire.virebackend.problem.ProblemFactory;
import com.vire.virebackend.security.filter.MdcLoggingFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order
public class FallbackExceptionHandler {

    private final ProblemFactory problemFactory;

    // 500 others
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAny(Exception exception, HttpServletRequest request) {
        var traceId = MDC.get(MdcLoggingFilter.TRACE_ID);
        var requestId = MDC.get(MdcLoggingFilter.REQUEST_ID);

        var status = 500;
        var method = request.getMethod();
        var path = request.getRequestURI();

        log.error("Unhandled exception: status={}, method={}, path={}, traceId={}, requestId={}",
                status, method, path, traceId, requestId, exception);

        return problemFactory.internal(traceId);
    }
}
