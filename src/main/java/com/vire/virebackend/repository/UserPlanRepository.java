package com.vire.virebackend.repository;

import com.vire.virebackend.entity.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserPlanRepository extends JpaRepository<UserPlan, UUID> {
}
