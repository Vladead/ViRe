package com.vire.virebackend.dto.admin.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserPlanSummaryDto(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("plan_id")
        UUID planId,

        @JsonProperty("start_date")
        LocalDateTime startDate,

        @JsonProperty("end_date")
        LocalDateTime endDate
) {
}
