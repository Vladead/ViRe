package com.vire.virebackend.controller;

import com.vire.virebackend.dto.session.CreateSessionRequest;
import com.vire.virebackend.dto.session.SessionDto;
import com.vire.virebackend.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/session")
@RequiredArgsConstructor
@Tag(name = "Sessions")
@SecurityRequirement(name = "bearerAuth")
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "Create session for current user")
    @PostMapping
    public ResponseEntity<SessionDto> create(
            Authentication auth,
            @RequestBody(required = false) @Valid CreateSessionRequest request,
            HttpServletRequest http
    ) {
        var sessionDto = sessionService.createSession(auth, request, http);
        return ResponseEntity.ok(sessionDto);
    }

    @Operation(summary = "List current user's sessions")
    @GetMapping
    public ResponseEntity<List<SessionDto>> list(Authentication auth) {
        return ResponseEntity.ok(sessionService.listMySessions(auth));
    }

    @Operation(summary = "End own session by id")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable UUID id) {
        var ended = sessionService.endMySession(auth, id);
        return ended ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
