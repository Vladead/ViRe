package com.vire.virebackend.mapper.admin.plan;

import com.vire.virebackend.dto.admin.plan.UserPlanSummaryDto;
import com.vire.virebackend.entity.UserPlan;

public final class UserPlanSummaryMapper {

    private UserPlanSummaryMapper() {
    }

    public UserPlanSummaryDto toDto(UserPlan userPlan) {
        return UserPlanSummaryDto.builder()
                .id(userPlan.getId())
                .planId(userPlan.getPlan().getId())
                .startDate(userPlan.getStartDate())
                .endDate(userPlan.getEndDate())
                .build();
    }
}
