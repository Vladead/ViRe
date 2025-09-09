package com.vire.virebackend.mapper;

import com.vire.virebackend.dto.user.UserDto;
import com.vire.virebackend.entity.Role;
import com.vire.virebackend.entity.User;
import com.vire.virebackend.security.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(CustomUserDetails userDetails) {
        return new UserDto(
                userDetails.getUser().getId(),
                userDetails.getUsername(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList()
        );
    }
}
