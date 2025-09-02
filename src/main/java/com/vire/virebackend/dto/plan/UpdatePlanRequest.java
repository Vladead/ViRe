package com.vire.virebackend.dto.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Update an existing plan request")
public record UpdatePlanRequest(
        @JsonProperty("name")
        @NotBlank
        @Size(max = 64)
        @Pattern(regexp = "^[A-Za-z0-9\\s_-]+$")
        String name,

        @JsonProperty("price")
        @NotNull
        @DecimalMin(value = "0.00")
        @Digits(integer = 12, fraction = 2)
        BigDecimal price,

        @JsonProperty("duration_days")
        @NotNull
        @Positive
        Integer durationDays
) {
}
