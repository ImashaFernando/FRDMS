package com.frdms.frdms.victim_management.repository;

import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ReliefRequirementRepository
        extends JpaRepository<ReliefRequirement, Long> {

    /*
     * Find the relief requirement belonging to a particular
     * needs assessment.
     *
     * This is important because when the Relief Coordinator
     * opens the same needs assessment again, the previously
     * saved quantities must be loaded instead of creating
     * a new empty/default requirement.
     */
    Optional<ReliefRequirement> findByNeedsAssessment(
            NeedsAssessment needsAssessment
    );

    /*
     * Find all relief requirements belonging to a victim.
     */
    List<ReliefRequirement> findByVictim(
            Victim victim
    );

    /*
     * Find relief requirements by status.
     */
    List<ReliefRequirement> findByStatus(
            String status
    );

    /*
     * Get relief requirements by status,
     * newest requirements first.
     */
    List<ReliefRequirement> findByStatusOrderByCreatedAtDesc(
            String status
    );

    /*
     * Get all relief requirements ordered from newest
     * to oldest.
     */
    List<ReliefRequirement> findAllByOrderByCreatedAtDesc();
}