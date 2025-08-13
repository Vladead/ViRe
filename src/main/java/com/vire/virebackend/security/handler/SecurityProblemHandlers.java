package com.vire.virebackend.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vire.virebackend.problem.ProblemFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ProblemDetail;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class SecurityProblemHandlers {

    private final ProblemFactory problemFactory;

    private final ObjectMapper objectMapper;

    /**
     * Prepare http response with error description in RFC 7807 format
     *
     * @param response      injectable response object
     * @param problemDetail
     * @param status
     * @throws IOException
     */
    private void write(HttpServletResponse response, ProblemDetail problemDetail, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/problem+json");
        objectMapper.writeValue(response.getOutputStream(), problemDetail);
    }

    @Bean
    public AuthenticationEntryPoint handleAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            var problemDetail = problemFactory.unauthorized();
            write(response, problemDetail, HttpServletResponse.SC_UNAUTHORIZED);
        };
    }

    @Bean
    public AccessDeniedHandler handleAccessDenied() {
        return (request, response, accessDeniedException) -> {
            var problemDetail = problemFactory.forbidden();
            write(response, problemDetail, HttpServletResponse.SC_FORBIDDEN);
        };
    }
}
