package com.vire.virebackend.mapper;

import com.vire.virebackend.dto.node.NodeDto;
import com.vire.virebackend.entity.Node;

public final class NodeMapper {

    private NodeMapper() {
    }

    public static NodeDto toDto(Node node) {
        return new NodeDto(
                node.getId(),
                node.getName(),
                node.getRegion(),
                node.getHost(),
                node.getStatus(),
                node.getLastHeartbeatAt(),
                node.getCreatedAt()
        );
    }
}
