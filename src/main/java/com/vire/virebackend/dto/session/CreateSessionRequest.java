package com.vire.virebackend.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Create session request")
public record CreateSessionRequest(
        @JsonProperty("device_name")
        String deviceName
) {
}
