package com.vire.virebackend.mapper;

import com.vire.virebackend.dto.session.SessionDto;
import com.vire.virebackend.entity.Session;

public class SessionMapper {

    private SessionMapper() {
    }

    public static SessionDto toDto(Session session) {
        return new SessionDto(
                session.getId(),
                session.getDeviceName(),
                session.getIp(),
                session.getCreatedAt(),
                session.getExpiresAt(),
                session.getIsActive()
        );
    }
}
