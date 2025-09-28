package com.vire.virebackend.dto.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Create new node request")
public record CreateNodeRequest(
        @JsonProperty("name")
        @NotBlank @Size(max = 64) @Pattern(regexp = "^[A-Za-z0-9._-]+$")
        String name,

        @JsonProperty("region")
        @NotBlank @Size(max = 32) @Pattern(regexp = "^[A-Za-z0-9._-]+$")
        String region,

        @JsonProperty("host")
        @NotBlank @Size(max = 255)
        String host
) {
}
