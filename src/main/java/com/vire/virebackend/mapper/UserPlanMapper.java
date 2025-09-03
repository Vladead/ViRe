package com.vire.virebackend.mapper;

import com.vire.virebackend.dto.plan.UserPlanDto;
import com.vire.virebackend.entity.UserPlan;

public final class UserPlanMapper {
    public static UserPlanDto toDto(UserPlan userPlan) {
        return new UserPlanDto(
                userPlan.getId(),
                userPlan.getUser().getId(),
                userPlan.getPlan().getId(),
                userPlan.getStartDate(),
                userPlan.getEndDate()
        );
    }
}
