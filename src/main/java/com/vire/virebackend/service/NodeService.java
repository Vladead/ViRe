package com.vire.virebackend.service;

import com.vire.virebackend.dto.node.CreateNodeRequest;
import com.vire.virebackend.dto.node.NodeCreatedResponse;
import com.vire.virebackend.dto.node.NodeDto;
import com.vire.virebackend.entity.Node;
import com.vire.virebackend.entity.NodeStatus;
import com.vire.virebackend.mapper.NodeMapper;
import com.vire.virebackend.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public NodeCreatedResponse create(CreateNodeRequest request) {
        if (nodeRepository.existsByNameIgnoreCase(request.name()))
            throw new DataIntegrityViolationException("Node name already exists");
        if (nodeRepository.existsByHostIgnoreCase(request.host()))
            throw new DataIntegrityViolationException("Node host already exists");

        var apiKey = generateApiKey();
        var node = Node.builder()
                .name(request.name().trim())
                .region(request.region().trim())
                .host(request.host().trim())
                .status(NodeStatus.INACTIVE)
                .apiKey(passwordEncoder.encode(apiKey)) // save encoded
                .build();

        nodeRepository.save(node);
        return new NodeCreatedResponse(node.getId(), apiKey); // response with actual key
    }

    @Transactional(readOnly = true)
    public List<NodeDto> listNodes() {
        return nodeRepository.findAll().stream().map(NodeMapper::toDto).toList();
    }

    private static String generateApiKey() {
        var random = new SecureRandom();
        var bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
