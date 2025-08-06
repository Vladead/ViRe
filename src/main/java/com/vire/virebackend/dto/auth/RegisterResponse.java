package com.vire.virebackend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Registration response")
public record RegisterResponse(
        String token
) {
}
