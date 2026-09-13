package com.frdms.frdms.victim_management.repository;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.entity.Victim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AffectedFamilyInformationRepository
        extends JpaRepository<AffectedFamilyInformation, Long> {

    Optional<AffectedFamilyInformation> findByVictim(Victim victim);
}