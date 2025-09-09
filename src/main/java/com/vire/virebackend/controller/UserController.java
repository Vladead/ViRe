package com.vire.virebackend.controller;

import com.vire.virebackend.dto.plan.UserPlanDto;
import com.vire.virebackend.dto.user.UserDto;
import com.vire.virebackend.mapper.UserMapper;
import com.vire.virebackend.security.CustomUserDetails;
import com.vire.virebackend.service.UserPlanService;
import com.vire.virebackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserPlanService userPlanService;

    @Operation(
            summary = "Get current user info from JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = UserDto.class)))
            }
    )
    @GetMapping("me")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(UserMapper.toDto(userDetails));
    }

    @Operation(summary = "Get current user plans")
    @GetMapping("plans")
    public ResponseEntity<List<UserPlanDto>> getCurrentUserPlans(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userPlanService.userSubscriptions(userDetails.getUser().getId()));
    }
}
