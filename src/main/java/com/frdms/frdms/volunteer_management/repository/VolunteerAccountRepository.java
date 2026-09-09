package com.frdms.frdms.volunteer_management.repository;

import com.frdms.frdms.volunteer_management.entity.VolunteerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerAccountRepository extends JpaRepository<VolunteerAccount, Long> {

    Optional<VolunteerAccount> findByEmail(String email);

    boolean existsByEmail(String email);
}