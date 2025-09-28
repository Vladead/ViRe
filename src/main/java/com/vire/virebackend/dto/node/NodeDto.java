package com.vire.virebackend.dto.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vire.virebackend.entity.NodeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Node representation")
public record NodeDto(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("name")
        String name,

        @JsonProperty("region")
        String region,

        @JsonProperty("host")
        String host,

        @JsonProperty("status")
        NodeStatus status,

        @JsonProperty("last_heartbeat_at")
        LocalDateTime lastHeartbeatAt,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
}
