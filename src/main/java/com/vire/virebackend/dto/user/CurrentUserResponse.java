package com.vire.virebackend.dto.user;

import com.vire.virebackend.security.CustomUserDetails;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;

@Builder
public record CurrentUserResponse(
        UUID id,
        String email,
        List<String> roles
) {
    public static CurrentUserResponse from(CustomUserDetails userDetails) {
        return new CurrentUserResponse(
                userDetails.getUser().getId(),
                userDetails.getUsername(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }
}
