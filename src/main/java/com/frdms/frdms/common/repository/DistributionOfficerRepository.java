package com.frdms.frdms.common.repository;

import com.frdms.frdms.common.entity.DistributionOfficerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistributionOfficerRepository
        extends JpaRepository<DistributionOfficerAccount, Long> {

    Optional<DistributionOfficerAccount> findByUsername(String username);

    boolean existsByUsername(String username);
}