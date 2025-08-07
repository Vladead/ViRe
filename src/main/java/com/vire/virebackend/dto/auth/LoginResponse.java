package com.vire.virebackend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Login response")
public record LoginResponse(
        UUID id,
        String token
) {
}
