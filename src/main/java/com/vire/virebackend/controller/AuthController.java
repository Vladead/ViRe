package com.vire.virebackend.controller;

import com.vire.virebackend.dto.auth.LoginRequest;
import com.vire.virebackend.dto.auth.LoginResponse;
import com.vire.virebackend.dto.auth.RegisterRequest;
import com.vire.virebackend.dto.auth.RegisterResponse;
import com.vire.virebackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authorization")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "New user registration",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successful registration",
                            content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "409", description = "Email already in use")
            }
    )
    @PostMapping("register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest http) {
        log.info("Registration attempt: email={}", request.email());
        var response = authService.register(request, http);
        log.info("Registration success: id={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Login user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class)))
            }
    )
    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        log.info("Login attempt: email={}", request.email());
        var response = authService.login(request, http);
        log.info("Login success: id={}", response.id());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout current session")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Logout all user sessions")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("logout-all")
    public ResponseEntity<Void> logoutAll(Authentication authentication) {
        authService.logoutAll(authentication);
        return ResponseEntity.noContent().build();
    }
}
