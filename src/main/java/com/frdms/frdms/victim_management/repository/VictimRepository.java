package com.frdms.frdms.victim_management.repository;

import com.frdms.frdms.victim_management.entity.Victim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VictimRepository
        extends JpaRepository<Victim, Long> {

    // Find victim by NIC number
    Optional<Victim> findByNicNumber(String nicNumber);

    // Find victim by username for victim login
    Optional<Victim> findByUsername(String username);
}