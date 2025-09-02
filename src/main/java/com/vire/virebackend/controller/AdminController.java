package com.vire.virebackend.controller;

import com.vire.virebackend.dto.plan.CreatePlanRequest;
import com.vire.virebackend.dto.plan.PlanDto;
import com.vire.virebackend.dto.user.UserDto;
import com.vire.virebackend.service.PlanService;
import com.vire.virebackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;
    private final PlanService planService;

    @Operation(summary = "List all users (admin)")
    @GetMapping("users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
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
}
