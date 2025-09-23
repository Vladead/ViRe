package com.vire.virebackend.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record DeactivateSessionResponse(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("was_active")
        Boolean wasActive
) {
}
