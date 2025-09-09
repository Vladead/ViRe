package com.vire.virebackend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "User info")
public record UserDto(
        UUID id,
        String email,
        List<String> roles
) {
}
