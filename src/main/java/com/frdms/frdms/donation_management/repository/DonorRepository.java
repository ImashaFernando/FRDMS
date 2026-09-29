package com.frdms.frdms.donation_management.repository;

import com.frdms.frdms.donation_management.entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByUsername(String username);

    boolean existsByUsername(String username);

    // US-D04: Search donor records
    List<Donor> findByFullNameContainingIgnoreCase(String fullName);
}