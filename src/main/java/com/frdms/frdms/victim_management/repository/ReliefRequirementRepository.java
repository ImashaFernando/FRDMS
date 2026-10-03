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
     */
    Optional<ReliefRequirement> findByNeedsAssessment(
            NeedsAssessment needsAssessment
    );

    /*
     * Find all relief requirements belonging to a victim.
     */
    List<ReliefRequirement> findByVictim(Victim victim);

    /*
     * Find relief requirements by status.
     */
    List<ReliefRequirement> findByStatus(String status);

    /*
     * Get approved relief requirements for Distribution Management.
     *
     * Newest requirements appear first.
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