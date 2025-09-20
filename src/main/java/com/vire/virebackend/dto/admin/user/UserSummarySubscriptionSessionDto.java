package com.vire.virebackend.dto.admin.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vire.virebackend.dto.admin.plan.UserPlanSummaryDto;
import com.vire.virebackend.dto.admin.session.SessionSummaryDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record UserSummarySubscriptionSessionDto(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("email")
        String email,

        @JsonProperty("username")
        String username,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("roles")
        List<String> roles,

        @JsonProperty("sessions")
        List<SessionSummaryDto> sessions,

        @JsonProperty("user_plans")
        List<UserPlanSummaryDto> userPlans
) {
}
