package com.vire.virebackend.problem;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProblemFactory {

    private final ProblemTypeResolver resolver;

    public ProblemDetail of(HttpStatus status, ProblemType type, String title, String detail) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(resolver.uri(type));
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return problemDetail;
    }

    public ProblemDetail validation(Map<String, ?> errors) {
        var problemDetail = of(
                HttpStatus.BAD_REQUEST,
                ProblemType.VALIDATION,
                "Validation failed",
                "One or more fields are invalid");
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    public ProblemDetail internal(UUID incidentId) {
        var problemDetail = of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemType.INTERNAL,
                "Internal server error",
                "If the problem persists, contact support and mention incidentId");
        problemDetail.setProperty("incidentId", incidentId != null ? incidentId : UUID.randomUUID());

        return problemDetail;
    }

    public ProblemDetail internal(String incidentId) {
        var problemDetail = of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemType.INTERNAL,
                "Internal server error",
                "If the problem persists, contact support and mention incidentId");
        problemDetail.setProperty(
                "incidentId",
                (incidentId != null && !incidentId.isBlank()) ? incidentId : UUID.randomUUID()
        );

        return problemDetail;
    }

    public ProblemDetail badRequest(String detail) {
        return of(
                HttpStatus.BAD_REQUEST,
                ProblemType.BAD_REQUEST,
                "Bad request",
                detail != null ? detail : "Malformed or invalid request");
    }

    public ProblemDetail unauthorized() {
        return of(
                HttpStatus.UNAUTHORIZED,
                ProblemType.AUTHENTICATION,
                "Unauthorized",
                "Authentication required or failed");
    }

    public ProblemDetail forbidden() {
        return of(
                HttpStatus.FORBIDDEN,
                ProblemType.FORBIDDEN,
                "Forbidden",
                "You don't have permission to perform this action");
    }

    public ProblemDetail notFound(String detail) {
        return of(
                HttpStatus.NOT_FOUND,
                ProblemType.NOT_FOUND,
                "Not found",
                detail != null ? detail : "Resource not found");
    }

    public ProblemDetail conflict(String detail) {
        return of(
                HttpStatus.CONFLICT,
                ProblemType.CONFLICT,
                "Conflict",
                detail != null ? detail : "Resource modified concurrently");
    }
}
