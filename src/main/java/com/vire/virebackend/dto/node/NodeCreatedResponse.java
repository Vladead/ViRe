package com.vire.virebackend.dto.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Node created response with one-time apiKey")
public record NodeCreatedResponse(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("api_key")
        String apiKey
) {
}
