package com.vire.virebackend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vire.virebackend.problem.ProblemFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RequestSizeLimitFilter extends OncePerRequestFilter {
    private final Long maxBytes;

    private ProblemFactory problemFactory;
    private ObjectMapper objectMapper;

    public RequestSizeLimitFilter(Long maxBytes) {
        this.maxBytes = maxBytes;
    }

    @Autowired
    protected void setProblemFactory(ProblemFactory problemFactory) {
        this.problemFactory = problemFactory;
    }

    @Autowired
    protected void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var contentLength = request.getContentLengthLong();
        if (contentLength > 0 && contentLength > maxBytes) {
            var problem = problemFactory.badRequest("Request body too large");
            response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
            response.setContentType("application/problem+json");
            objectMapper.writeValue(response.getOutputStream(), problem);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
