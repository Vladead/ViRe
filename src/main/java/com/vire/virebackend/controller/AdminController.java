package com.vire.virebackend.controller;

import com.vire.virebackend.dto.PageResponse;
import com.vire.virebackend.dto.admin.user.UpdateUserRolesRequest;
import com.vire.virebackend.dto.admin.user.UserSummaryDto;
import com.vire.virebackend.dto.admin.user.UserSummarySubscriptionSessionDto;
import com.vire.virebackend.dto.plan.CreatePlanRequest;
import com.vire.virebackend.dto.plan.PlanDto;
import com.vire.virebackend.dto.plan.UpdatePlanRequest;
import com.vire.virebackend.dto.session.DeactivateSessionResponse;
import com.vire.virebackend.mapper.PageResponseMapper;
import com.vire.virebackend.security.CustomUserDetails;
import com.vire.virebackend.service.AdminService;
import com.vire.virebackend.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final PlanService planService;
    private final AdminService adminService;

    @Operation(summary = "List users (paginated, createdAt DESC by default)")
    @GetMapping("users")
    public PageResponse<UserSummaryDto> listUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            HttpServletRequest request
    ) {
        var page = adminService.listUsers(pageable);
        return PageResponseMapper.toResponse(page, request);
    }

    @Operation(summary = "User profile (basic info, roles, active sessions, subscription summary)")
    @GetMapping("users/{id}")
    public ResponseEntity<UserSummarySubscriptionSessionDto> getUserProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getProfile(id));
    }

    @Operation(summary = "Update user roles (whitelist via DB: USER, ADMIN). Idempotent")
    @PutMapping("users/{id}/roles")
    public ResponseEntity<UserSummaryDto> updateUserRoles(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRolesRequest request,
            @AuthenticationPrincipal CustomUserDetails current
    ) {
        var updated = adminService.updateUserRoles(id, request.roles(), current.getUser().getId());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Terminate a specific user session")
    @DeleteMapping("users/{id}/sessions/{sessionId}")
    public ResponseEntity<DeactivateSessionResponse> terminateUserSession(
            @PathVariable UUID id,
            @PathVariable UUID sessionId
    ) {
        var response = adminService.deactivateSession(id, sessionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout user from all devices")
    @PostMapping("users/{id}/logout-all")
    public ResponseEntity<List<DeactivateSessionResponse>> logoutAllUserSessions(
            @PathVariable UUID id
    ) {
        var response = adminService.logoutAllUserSessions(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new plan")
    @PostMapping("plans")
    public ResponseEntity<PlanDto> create(@Valid @RequestBody CreatePlanRequest request) {
        var created = planService.createPlan(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/plans/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update an existing plan")
    @PutMapping("plans/{id}")
    public ResponseEntity<PlanDto> update(
            @Valid @RequestBody UpdatePlanRequest request,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(planService.updatePlan(request, id));
    }
}
