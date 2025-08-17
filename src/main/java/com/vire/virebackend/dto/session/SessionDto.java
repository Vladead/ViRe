package com.vire.virebackend.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Session representation")
public record SessionDto(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("device_name")
        String deviceName,

        @JsonProperty("ip")
        String ip,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("expires_at")
        LocalDateTime expiresAt,

        @JsonProperty("is_active")
        Boolean isActive
) {
}
