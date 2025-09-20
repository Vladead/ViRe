package com.vire.virebackend.service;

import com.vire.virebackend.dto.admin.session.SessionSummaryDto;
import com.vire.virebackend.dto.admin.user.UserSummaryDto;
import com.vire.virebackend.dto.admin.user.UserSummarySubscriptionSessionDto;
import com.vire.virebackend.mapper.admin.plan.UserPlanSummaryMapper;
import com.vire.virebackend.mapper.admin.session.SessionSummaryMapper;
import com.vire.virebackend.mapper.admin.user.UserSummaryMapper;
import com.vire.virebackend.mapper.admin.user.UserSummarySubscriptionSessionMapper;
import com.vire.virebackend.repository.SessionRepository;
import com.vire.virebackend.repository.UserPlanRepository;
import com.vire.virebackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final UserPlanRepository userPlanRepository;

    public Page<UserSummaryDto> listUsers(Pageable pageable) {
        var effective = normalize(pageable);
        var page = userRepository.findAll(effective);

        return page.map(UserSummaryMapper::toDto);
    }

    public Pageable normalize(Pageable pageable) {
        var size = Math.min(Math.max(pageable.getPageSize(), 1), 100); // 1..100
        var sort = pageable.getSort().isUnsorted()
                ? Sort.by(Sort.Direction.DESC, "createdAt")
                : pageable.getSort();
        return PageRequest.of(pageable.getPageNumber(), size, sort);
    }

    @Transactional(readOnly = true)
    public UserSummarySubscriptionSessionDto getProfile(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        var sessions = sessionRepository.findAllByUserIdAndIsActiveTrue(userId).stream()
                .map(SessionSummaryMapper::toDto)
                .sorted(Comparator.comparing(
                        SessionSummaryDto::lastActivityAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .toList();

        var userPlans = userPlanRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(UserPlanSummaryMapper::toDto)
                .toList();

        return UserSummarySubscriptionSessionMapper.toDto(user, sessions, userPlans);
    }
}
