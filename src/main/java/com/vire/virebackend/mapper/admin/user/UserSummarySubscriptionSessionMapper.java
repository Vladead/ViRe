package com.vire.virebackend.mapper.admin.user;

import com.vire.virebackend.dto.admin.plan.UserPlanSummaryDto;
import com.vire.virebackend.dto.admin.session.SessionSummaryDto;
import com.vire.virebackend.dto.admin.user.UserSummarySubscriptionSessionDto;
import com.vire.virebackend.entity.Role;
import com.vire.virebackend.entity.User;

import java.util.List;

public final class UserSummarySubscriptionSessionMapper {

    private UserSummarySubscriptionSessionMapper() {
    }

    public static UserSummarySubscriptionSessionDto toDto(
            User user,
            List<SessionSummaryDto> sessions,
            List<UserPlanSummaryDto> userPlans
    ) {
        return UserSummarySubscriptionSessionDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream().map(Role::getName).sorted().toList())
                .sessions(sessions)
                .userPlans(userPlans)
                .build();
    }
}
