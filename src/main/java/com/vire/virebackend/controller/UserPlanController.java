package com.vire.virebackend.controller;

import com.vire.virebackend.dto.plan.UserPlanDto;
import com.vire.virebackend.security.CustomUserDetails;
import com.vire.virebackend.service.UserPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/plans")
@RequiredArgsConstructor
@Tag(name = "User plans")
@SecurityRequirement(name = "bearerAuth")
public class UserPlanController {

    private final UserPlanService userPlanService;

    @Operation(summary = "Subscribe current user to the plan")
    @PostMapping("{id}/subscribe")
    public ResponseEntity<UserPlanDto> subscribe(
            @PathVariable("id") UUID planId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        var userId = userDetails.getUser().getId();
        var response = userPlanService.subscribe(planId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
