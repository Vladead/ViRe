package com.vire.virebackend.mapper.admin.user;

import com.vire.virebackend.dto.admin.user.UserSummaryDto;
import com.vire.virebackend.entity.Role;
import com.vire.virebackend.entity.User;

public final class UserSummaryMapper {

    private UserSummaryMapper() {
    }

    public static UserSummaryDto toDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .toList())
                .build();
    }
}
