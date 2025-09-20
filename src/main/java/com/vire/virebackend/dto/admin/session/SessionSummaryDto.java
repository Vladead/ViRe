package com.vire.virebackend.dto.admin.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SessionSummaryDto(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("device_name")
        String deviceName,

        @JsonProperty("user_agent")
        String userAgent,

        @JsonProperty("ip")
        String ip,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("expires_at")
        LocalDateTime expiresAt,

        @JsonProperty("last_activity_at")
        LocalDateTime lastActivityAt,

        @JsonProperty("is_active")
        Boolean isActive
) {
}
