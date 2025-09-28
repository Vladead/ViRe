package com.vire.virebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "nodes")
public class Node extends BaseEntity {

    @Column(name = "name", unique = true, nullable = false, length = 64)
    private String name;

    @Column(name = "region", nullable = false, length = 32)
    private String region;

    @Column(name = "host", unique = true, nullable = false)
    private String host;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private NodeStatus status;

    @Column(name = "last_heartbeat_at")
    private LocalDateTime lastHeartbeatAt;

    @Column(name = "api_key_hash", nullable = false, length = 100)
    private String apiKey;
}
