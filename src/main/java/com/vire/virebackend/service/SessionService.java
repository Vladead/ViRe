package com.vire.virebackend.service;

import com.vire.virebackend.config.JwtProperties;
import com.vire.virebackend.dto.session.CreateSessionRequest;
import com.vire.virebackend.dto.session.SessionDto;
import com.vire.virebackend.entity.Session;
import com.vire.virebackend.mapper.SessionMapper;
import com.vire.virebackend.repository.SessionRepository;
import com.vire.virebackend.repository.UserRepository;
import com.vire.virebackend.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public SessionDto createSession(Authentication auth, CreateSessionRequest request, HttpServletRequest http) {
        var userDetails = (CustomUserDetails) auth.getPrincipal();
        var user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var ip = extractClientIp(http);
        var deviceName = (request != null && request.deviceName() != null && !request.deviceName().isBlank())
                ? request.deviceName().trim()
                : "Unknown device";

        var session = Session.builder()
                .user(user)
                .deviceName(deviceName)
                .ip(ip)
                .expiresAt(LocalDateTime.now().plus(jwtProperties.expiration()))
                .isActive(true)
                .build();

        session = sessionRepository.save(session);
        return SessionMapper.toDto(session);
    }

    @Transactional(readOnly = true)
    public List<SessionDto> listMySessions(Authentication auth) {
        var userDetails = (CustomUserDetails) auth.getPrincipal();
        return sessionRepository.findAllByUserIdOrderByCreatedAtDesc(userDetails.getUser().getId())
                .stream().map(SessionMapper::toDto).toList();
    }

    @Transactional
    public Boolean endMySession(Authentication auth, UUID sessionId) {
        var userDetails = (CustomUserDetails) auth.getPrincipal();
        var session = sessionRepository.findByIdAndUserId(sessionId, userDetails.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        if (!session.getIsActive())
            return false;

        session.setIsActive(false);

        return true;
    }

    private String extractClientIp(HttpServletRequest request) {
        // if using reverse proxy, then take into consideration forwarded
        return request.getRemoteAddr();
    }
}
