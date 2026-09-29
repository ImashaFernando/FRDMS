package com.frdms.frdms.common.repository;

import com.frdms.frdms.common.entity.ProjectManagerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectManagerRepository
        extends JpaRepository<ProjectManagerAccount, Long> {

    Optional<ProjectManagerAccount> findByEmail(String email);

    boolean existsByEmail(String email);
}