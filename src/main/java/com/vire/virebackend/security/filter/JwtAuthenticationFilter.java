package com.vire.virebackend.security.filter;

import com.vire.virebackend.config.SessionProperties;
import com.vire.virebackend.repository.SessionRepository;
import com.vire.virebackend.repository.UserRepository;
import com.vire.virebackend.security.CustomUserDetails;
import com.vire.virebackend.security.JwtService;
import com.vire.virebackend.security.SessionActivityTracker;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionActivityTracker sessionActivityTracker;
    private final SessionProperties sessionProperties;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final var jwt = authHeader.substring(7);

        try {
            var userId = jwtService.extractUserId(jwt);
            var jti = jwtService.extractJti(jwt);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var user = userRepository.findById(userId)
                        .orElseThrow(() -> new AuthenticationException("User not found") {
                        });

                if (jwtService.isTokenValid(jwt, user)) {
                    // Ensure server-side session is active and not expired for this jti
                    var session = sessionRepository.findByJtiAndIsActive(jti, true)
                            .orElseThrow(() -> new AuthenticationException("Session inactive") {});

                    if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now())) {
                        throw new AuthenticationException("Session expired") {
                        };
                    }

                    if (sessionActivityTracker.shouldUpdate(jti, sessionProperties.activityUpdateThreshold())) {
                        sessionRepository.findByJtiAndIsActive(jti, true).ifPresent(s -> {
                            s.setLastActivityAt(LocalDateTime.now());
                            sessionRepository.save(s);
                        });
                    }

                    var userDetails = new CustomUserDetails(user);

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException exception) {
            throw new AuthenticationException("Invalid JWT", exception) {
            };
        }

        filterChain.doFilter(request, response);
    }
}
