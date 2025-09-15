package com.vire.virebackend.service;

import com.vire.virebackend.dto.auth.LoginRequest;
import com.vire.virebackend.dto.auth.LoginResponse;
import com.vire.virebackend.dto.auth.RegisterRequest;
import com.vire.virebackend.dto.auth.RegisterResponse;
import com.vire.virebackend.entity.Session;
import com.vire.virebackend.entity.User;
import com.vire.virebackend.repository.RoleRepository;
import com.vire.virebackend.repository.SessionRepository;
import com.vire.virebackend.repository.UserRepository;
import com.vire.virebackend.security.CustomUserDetails;
import com.vire.virebackend.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public RegisterResponse register(RegisterRequest request, HttpServletRequest http) {
        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        var userRole = roleRepository.findByName("USER")
                        .orElseThrow(() -> new IllegalStateException("Default role USER not found"));
        user.getRoles().add(userRole);

        userRepository.save(user);

        var jti = UUID.randomUUID();
        var userAgent = http.getHeader("User-Agent");

        var session = Session.builder()
                .user(user)
                .jti(jti)
                .isActive(true)
                .userAgent(userAgent != null ? userAgent : "")
                .ip(http.getRemoteAddr())
                .deviceName("Unknown device") // todo: determine by User-Agent
                .lastActivityAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        sessionRepository.save(session);

        var token = jwtService.generateToken(user, jti);
        return new RegisterResponse(user.getId(), token);
    }

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest http) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var user = userDetails.getUser();

        var jti = UUID.randomUUID();
        var userAgent = http.getHeader("User-Agent");

        var session = Session.builder()
                .user(user)
                .jti(jti)
                .isActive(true)
                .userAgent(userAgent != null ? userAgent : "")
                .ip(http.getRemoteAddr())
                .deviceName("Unknown device") // todo: determine by User-Agent
                .lastActivityAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        sessionRepository.save(session);

        var jwt = jwtService.generateToken(user, jti);
        return new LoginResponse(user.getId(), jwt);
    }

    @Transactional
    public void logout(String authHeader) {
        if (authHeader == null) return;

        var parts = authHeader.trim().split("\\s+", 2);
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("Bearer")) return;

        var token = parts[1].trim();
        if (token.isEmpty()) return;

        final UUID jti;
        try {
            jti = jwtService.extractJti(token);
        } catch (Exception e) {
            return;
        }

        sessionRepository.findByJtiAndIsActive(jti, true).ifPresent(session -> {
            session.setIsActive(false);
            sessionRepository.save(session);
        });
    }

    @Transactional
    public void logoutAll(Authentication authentication) {
        var principal = (CustomUserDetails) authentication.getPrincipal();
        var userId = principal.getUser().getId();

        var activeSessions = sessionRepository.findAllByUserIdAndIsActiveTrue(userId);
        if (!activeSessions.isEmpty()) {
            for (var s : activeSessions) {
                s.setIsActive(false);
            }
            sessionRepository.saveAll(activeSessions);
        }
    }
}
