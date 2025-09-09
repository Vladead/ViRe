package com.vire.virebackend.mapper;

import com.vire.virebackend.dto.plan.PlanDto;
import com.vire.virebackend.entity.Plan;

public class PlanMapper {

    private PlanMapper() {
    }

    public static PlanDto toDto(Plan plan) {
        return new PlanDto(
                plan.getId(),
                plan.getName(),
                plan.getPrice(),
                plan.getDurationDays()
        );
    }
}
