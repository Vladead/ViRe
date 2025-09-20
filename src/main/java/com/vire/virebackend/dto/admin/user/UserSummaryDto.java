package com.vire.virebackend.dto.admin.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record UserSummaryDto(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("email")
        String email,

        @JsonProperty("username")
        String username,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("roles")
        List<String> roles
) {
}
