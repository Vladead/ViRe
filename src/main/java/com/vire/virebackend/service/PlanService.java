package com.vire.virebackend.service;

import com.vire.virebackend.dto.plan.PlanDto;
import com.vire.virebackend.mapper.PlanMapper;
import com.vire.virebackend.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    @Transactional(readOnly = true)
    public List<PlanDto> getAllPlans() {
        return planRepository.findAll()
                .stream().map(PlanMapper::toDto).toList();
    }
}
