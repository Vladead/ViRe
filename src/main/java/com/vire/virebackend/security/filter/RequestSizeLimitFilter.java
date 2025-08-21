package com.vire.virebackend.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RequestSizeLimitFilter extends OncePerRequestFilter {
    private final Long maxBytes;

    public RequestSizeLimitFilter(Long maxBytes) {
        this.maxBytes = maxBytes;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var contentLength = request.getContentLengthLong();
        if (contentLength > 0 && contentLength > maxBytes) {
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "Request body too large");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
