package com.vire.virebackend.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    public static final String TRACE_ID = "traceId";
    public static final String TRACE_HEADER = "X-Trace-Id";
    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var traceId = getOrGenerate(request, TRACE_HEADER);
        var requestId = getOrGenerate(request, REQUEST_HEADER);

        MDC.put(TRACE_ID, traceId);
        MDC.put(REQUEST_ID, requestId);

        response.setHeader(TRACE_HEADER, traceId);
        response.setHeader(REQUEST_HEADER, requestId);

        try (var ignored = MDC.putCloseable(TRACE_ID, traceId);
             var ignored1 = MDC.putCloseable(REQUEST_ID, requestId)) {
            filterChain.doFilter(request, response);
        }
    }

    private String getOrGenerate(HttpServletRequest request, String header) {
        var value = request.getHeader(header);
        return (value != null && !value.isBlank()) ? value : UUID.randomUUID().toString();
    }
}
