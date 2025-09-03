package com.vire.virebackend.service;

import com.vire.virebackend.dto.plan.UserPlanDto;
import com.vire.virebackend.entity.UserPlan;
import com.vire.virebackend.mapper.UserPlanMapper;
import com.vire.virebackend.repository.PlanRepository;
import com.vire.virebackend.repository.UserPlanRepository;
import com.vire.virebackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPlanService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final UserPlanRepository userPlanRepository;

    @Transactional
    public UserPlanDto subscribe(UUID planId, UUID userId) {
        var plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));

        var userPlan = UserPlan.builder()
                .user(userRepository.getReferenceById(userId))
                .plan(plan)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(plan.getDurationDays()))
                .build();

        return UserPlanMapper.toDto(userPlanRepository.save(userPlan));
    }
}
