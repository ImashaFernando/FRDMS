package com.frdms.frdms.shelter_management.repository;

import com.frdms.frdms.shelter_management.entity.StaffAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository
        extends JpaRepository<StaffAccount, Long> {

    Optional<StaffAccount> findByEmail(String email);

    boolean existsByEmail(String email);
}
