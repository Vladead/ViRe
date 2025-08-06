package com.vire.virebackend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response")
public record LoginResponse(
        String token
) {
}
