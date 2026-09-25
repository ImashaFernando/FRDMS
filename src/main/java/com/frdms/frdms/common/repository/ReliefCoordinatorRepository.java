package com.frdms.frdms.common.repository;

import com.frdms.frdms.common.entity.ReliefCoordinatorAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReliefCoordinatorRepository
        extends JpaRepository<ReliefCoordinatorAccount, Long> {

    Optional<ReliefCoordinatorAccount> findByUsername(String username);

    boolean existsByUsername(String username);
}