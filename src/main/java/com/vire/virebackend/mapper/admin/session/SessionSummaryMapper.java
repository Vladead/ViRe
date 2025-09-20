package com.vire.virebackend.mapper.admin.session;

import com.vire.virebackend.dto.admin.session.SessionSummaryDto;
import com.vire.virebackend.entity.Session;

public final class SessionSummaryMapper {

    private SessionSummaryMapper() {
    }

    public static SessionSummaryDto toDto(Session session) {
        return SessionSummaryDto.builder()
                .id(session.getId())
                .deviceName(session.getDeviceName())
                .userAgent(session.getUserAgent())
                .ip(session.getIp())
                .createdAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .lastActivityAt(session.getLastActivityAt())
                .isActive(session.getIsActive())
                .build();
    }
}
