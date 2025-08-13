package com.vire.virebackend.problem;

public enum ProblemType {

    VALIDATION("validation-error"),
    BAD_REQUEST("bad-request"),
    AUTHENTICATION("authentication"),
    FORBIDDEN("forbidden"),
    NOT_FOUND("not-found"),
    INTERNAL("internal");

    private final String slug;

    ProblemType(String slug) {
        this.slug = slug;
    }

    public String slug() {
        return slug;
    }
}
