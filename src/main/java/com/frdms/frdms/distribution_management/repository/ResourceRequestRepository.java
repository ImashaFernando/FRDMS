package com.frdms.frdms.distribution_management.repository;

import com.frdms.frdms.distribution_management.entity.ResourceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRequestRepository
        extends JpaRepository<ResourceRequest, Long> {
}