package com.frdms.frdms.victim_management.repository;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.Victim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NeedsAssessmentRepository
        extends JpaRepository<NeedsAssessment, Long> {

    Optional<NeedsAssessment> findTopByVictimOrderByIdDesc(Victim victim);
}