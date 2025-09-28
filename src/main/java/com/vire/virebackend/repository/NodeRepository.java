package com.vire.virebackend.repository;

import com.vire.virebackend.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NodeRepository extends JpaRepository<Node, UUID> {

    Boolean existsByNameIgnoreCase(String name);

    Boolean existsByHostIgnoreCase(String host);
}
