package com.vire.virebackend.service;

import com.vire.virebackend.dto.plan.CreatePlanRequest;
import com.vire.virebackend.dto.plan.PlanDto;
import com.vire.virebackend.entity.Plan;
import com.vire.virebackend.mapper.PlanMapper;
import com.vire.virebackend.repository.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    @Transactional(readOnly = true)
    public List<PlanDto> getAllPlans() {
        return planRepository.findAll()
                .stream().map(PlanMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PlanDto getPlan(UUID id) {
        var plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));

        return PlanMapper.toDto(plan);
    }

    @Transactional
    public PlanDto createPlan(CreatePlanRequest request) {
        if (planRepository.existsByNameIgnoreCase(request.name())) {
            throw new DataIntegrityViolationException("Plan name already exists");
        }

        var plan = Plan.builder()
                .name(request.name())
                .price(request.price())
                .durationDays(request.durationDays())
                .build();

        return PlanMapper.toDto(planRepository.save(plan));
    }
}
