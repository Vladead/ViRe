package com.vire.virebackend.config;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "session")
public record SessionProperties(
        @NotNull
        @DurationMin(seconds = 1)
        Duration activityUpdateThreshold
) {
}
