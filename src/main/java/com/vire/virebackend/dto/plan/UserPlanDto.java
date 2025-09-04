package com.vire.virebackend.dto.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "User plan representation")
public record UserPlanDto(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("user_id")
        UUID userId,

        @JsonProperty("plan_id")
        UUID planId,

        @JsonProperty("start_date")
        LocalDateTime startDate,

        @JsonProperty("end_date")
        LocalDateTime endDate
) {
}
