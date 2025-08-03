package com.vire.virebackend.dto.auth;

public record RegisterRequest(
        String email,
        String password
) {
}
