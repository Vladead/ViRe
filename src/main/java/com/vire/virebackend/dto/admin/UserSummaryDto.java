package com.vire.virebackend.dto.admin;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record UserSummaryDto(
        UUID id,
        String email,
        String username,
        LocalDateTime createdAt,
        List<String> roles
) {
}
