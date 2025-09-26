package com.vire.virebackend.repository;

import com.vire.virebackend.entity.UserPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPlanRepository extends JpaRepository<UserPlan, UUID> {

    List<UserPlan> findByUserIdOrderByStartDateDesc(UUID userId);

    Optional<UserPlan> findFirstByUserIdAndEndDateAfterOrderByEndDateDesc(UUID userId, LocalDateTime now);

    Page<UserPlan> findByUserIdOrderByStartDateDesc(UUID userId, Pageable pageable);
}
