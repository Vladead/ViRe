package com.vire.virebackend.repository;

import com.vire.virebackend.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    List<Session> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Session> findByIdAndUserId(UUID id, UUID userId);

    Optional<Session> findByJtiAndIsActive(UUID jti, boolean isActive);

    List<Session> findAllByUserIdAndIsActiveTrue(UUID userId);
}
