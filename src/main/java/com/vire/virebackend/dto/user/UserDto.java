package com.vire.virebackend.dto.user;

import com.vire.virebackend.entity.Role;
import com.vire.virebackend.entity.User;
import com.vire.virebackend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "User info")
public record UserDto(
        UUID id,
        String email,
        List<String> roles
) {

    public static UserDto from(CustomUserDetails userDetails) {
        return new UserDto(
                userDetails.getUser().getId(),
                userDetails.getUsername(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList()
        );
    }
}
