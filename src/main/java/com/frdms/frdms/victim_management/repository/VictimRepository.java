package com.frdms.frdms.victim_management.repository;

import com.frdms.frdms.victim_management.entity.Victim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VictimRepository
        extends JpaRepository<Victim, Long> {

    Optional<Victim> findByNicNumber(String nicNumber);
}