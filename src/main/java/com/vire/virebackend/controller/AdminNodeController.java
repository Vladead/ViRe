package com.vire.virebackend.controller;

import com.vire.virebackend.dto.node.CreateNodeRequest;
import com.vire.virebackend.dto.node.NodeCreatedResponse;
import com.vire.virebackend.dto.node.NodeDto;
import com.vire.virebackend.service.NodeService;
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
@RequestMapping("api/admin/nodes")
@RequiredArgsConstructor
@Tag(name = "Admin Nodes")
@SecurityRequirement(name = "bearerAuth")
public class AdminNodeController {

    private final NodeService nodeService;

    @Operation(summary = "Create a new node; returns plaintext apiKey only once")
    @PostMapping
    public ResponseEntity<NodeCreatedResponse> create(@Valid @RequestBody CreateNodeRequest request) {
        var created = nodeService.create(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "List all nodes")
    @GetMapping
    public List<NodeDto> list() {
        return nodeService.listNodes();
    }
}
