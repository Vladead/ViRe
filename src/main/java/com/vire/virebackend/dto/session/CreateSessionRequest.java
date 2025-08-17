package com.vire.virebackend.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Create session request")
public record CreateSessionRequest(
        @JsonProperty("device_name")
        @Size(max = 100)
        String deviceName
) {
}
