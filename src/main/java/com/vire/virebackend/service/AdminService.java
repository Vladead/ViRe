package com.vire.virebackend.service;

import com.vire.virebackend.dto.admin.session.SessionSummaryDto;
import com.vire.virebackend.dto.admin.user.UserSummaryDto;
import com.vire.virebackend.dto.admin.user.UserSummarySubscriptionSessionDto;
import com.vire.virebackend.dto.session.DeactivateSessionResponse;
import com.vire.virebackend.entity.Role;
import com.vire.virebackend.mapper.admin.plan.UserPlanSummaryMapper;
import com.vire.virebackend.mapper.admin.session.SessionSummaryMapper;
import com.vire.virebackend.mapper.admin.user.UserSummaryMapper;
import com.vire.virebackend.mapper.admin.user.UserSummarySubscriptionSessionMapper;
import com.vire.virebackend.repository.RoleRepository;
import com.vire.virebackend.repository.SessionRepository;
import com.vire.virebackend.repository.UserPlanRepository;
import com.vire.virebackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final UserPlanRepository userPlanRepository;
    private final RoleRepository roleRepository;

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

    @Transactional
    public UserSummaryDto updateUserRoles(UUID targetUserId, List<String> requestedRoles, UUID actorUserId) {
        var user = userRepository.findById(targetUserId)
                .orElseThrow(EntityNotFoundException::new);

        // normalize to upper and unique
        var requested = requestedRoles.stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim().toUpperCase())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        var targetCurrentRoles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        if (targetCurrentRoles.equals(requested)) {
            return UserSummaryMapper.toDto(user); // no-op
        }

        var isSelfChange = Objects.equals(actorUserId, targetUserId);
        var targetHasAdmin = targetCurrentRoles.contains("ADMIN");
        var requestedHasAdmin = requested.contains("ADMIN");

        if (isSelfChange && targetHasAdmin && !requestedHasAdmin) {
            throw new AuthorizationDeniedException("You cannot remove your own ADMIN role");
        }

        if (targetHasAdmin && !requestedHasAdmin) {
            var admins = userRepository.countByRoles_Name("ADMIN");
            if (admins <= 1) {
                throw new AuthorizationDeniedException("System must have at least one ADMIN");
            }
        }

        var roles = roleRepository.findByNameIn(requested);
        var foundedNames = roles.stream().map(Role::getName).collect(Collectors.toSet());
        if (!foundedNames.equals(requested)) {
            var unknown = new LinkedHashSet<>(requested);
            unknown.removeAll(foundedNames);
            throw new IllegalArgumentException("Unknown roles: " + String.join(", ", unknown));
        }

        user.getRoles().clear();
        user.getRoles().addAll(new HashSet<>(roles));

        return UserSummaryMapper.toDto(user);
    }

    @Transactional
    public DeactivateSessionResponse deactivateSession(UUID userId, UUID sessionId) {
        var session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(EntityNotFoundException::new);

        var wasActive = session.getIsActive();
        if (wasActive) {
            session.setIsActive(false);
        }

        return DeactivateSessionResponse.builder()
                .id(sessionId)
                .wasActive(wasActive)
                .build();
    }

    @Transactional
    public List<DeactivateSessionResponse> logoutAllUserSessions(UUID userId) {
        var allSessions = sessionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        var responses = new ArrayList<DeactivateSessionResponse>(allSessions.size());
        var changed = false;
        for (var s : allSessions) {
            var wasActive = s.getIsActive();
            if (wasActive) {
                s.setIsActive(false);
                changed = true;
            }
            responses.add(
                    DeactivateSessionResponse.builder()
                            .id(s.getId())
                            .wasActive(wasActive)
                            .build()
            );
        }
        if (changed) {
            sessionRepository.saveAll(allSessions);
        }
        return responses;
    }
}
