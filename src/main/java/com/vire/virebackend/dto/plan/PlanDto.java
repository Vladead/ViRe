package com.vire.virebackend.dto.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Plan representation")
public record PlanDto(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("name")
        String name,

        @JsonProperty("price")
        BigDecimal price,

        @JsonProperty("duration_days")
        Integer durationDays
) {
}
