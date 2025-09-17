package com.vire.virebackend.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionActivityTracker {
    private final Map<UUID, Instant> lastWriteByJti = new ConcurrentHashMap<>();

    public Boolean shouldUpdate(UUID jti, Duration threshold) {
        var now = Instant.now();
        var last = lastWriteByJti.get(jti);
        if (last == null || Duration.between(last, now).compareTo(threshold) >= 0) {
            lastWriteByJti.put(jti, now);
            return true;
        }
        return false;
    }
}
