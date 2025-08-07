package com.vire.virebackend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Registration response")
public record RegisterResponse(
        UUID id,
        String token
) {
}
