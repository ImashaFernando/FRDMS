package com.frdms.frdms.distribution_management.repository;

import com.frdms.frdms.distribution_management.entity.ReliefRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReliefRequirementRepository
        extends JpaRepository<ReliefRequirement, Long> {
}