package com.vire.virebackend.dto.admin.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateUserRolesRequest(
        @JsonProperty("roles")
        @NotNull
        List<String> roles
) {
}
