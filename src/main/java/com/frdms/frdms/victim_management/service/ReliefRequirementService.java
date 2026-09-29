package com.frdms.frdms.victim_management.service;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.repository.ReliefRequirementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("victimReliefRequirementService")
public class ReliefRequirementService {

    private final ReliefRequirementRepository reliefRequirementRepository;

    public ReliefRequirementService(
            ReliefRequirementRepository reliefRequirementRepository) {

        this.reliefRequirementRepository = reliefRequirementRepository;
    }

    // ---------------------------------------------------------
    // CREATE / SAVE RELIEF REQUIREMENT
    // ---------------------------------------------------------

    public ReliefRequirement createReliefRequirement(
            ReliefRequirement reliefRequirement) {

        return reliefRequirementRepository.save(reliefRequirement);
    }

    // ---------------------------------------------------------
    // GET RELIEF REQUIREMENT BY ID
    // ---------------------------------------------------------

    public ReliefRequirement getReliefRequirementById(Long id) {

        return reliefRequirementRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Relief requirement not found with ID: " + id
                        )
                );
    }

    // ---------------------------------------------------------
    // GET RELIEF REQUIREMENT BY NEEDS ASSESSMENT
    // ---------------------------------------------------------

    public Optional<ReliefRequirement> getByNeedsAssessment(
            NeedsAssessment needsAssessment) {

        return reliefRequirementRepository
                .findByNeedsAssessment(needsAssessment);
    }

    // ---------------------------------------------------------
    // GET ALL RELIEF REQUIREMENTS FOR A VICTIM
    // ---------------------------------------------------------

    public List<ReliefRequirement> getByVictim(Victim victim) {

        return reliefRequirementRepository.findByVictim(victim);
    }

    // ---------------------------------------------------------
    // GET ALL RELIEF REQUIREMENTS
    // ---------------------------------------------------------

    public List<ReliefRequirement> getAllReliefRequirements() {

        return reliefRequirementRepository.findAll();
    }

    // ---------------------------------------------------------
    // GET BY STATUS
    // ---------------------------------------------------------

    public List<ReliefRequirement> getByStatus(String status) {

        return reliefRequirementRepository.findByStatus(status);
    }

    // ---------------------------------------------------------
    // UPDATE RELIEF REQUIREMENT
    // ---------------------------------------------------------

    public ReliefRequirement updateReliefRequirement(
            Long id,
            String reliefItems,
            String victimMessage,
            String status) {

        ReliefRequirement existing =
                getReliefRequirementById(id);

        existing.setReliefItems(reliefItems);
        existing.setVictimMessage(victimMessage);

        if (status != null && !status.trim().isEmpty()) {
            existing.setStatus(status);
        }

        return reliefRequirementRepository.save(existing);
    }

    // ---------------------------------------------------------
    // DELETE RELIEF REQUIREMENT
    // ---------------------------------------------------------

    public void deleteReliefRequirement(Long id) {

        if (!reliefRequirementRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Relief requirement not found with ID: " + id
            );
        }

        reliefRequirementRepository.deleteById(id);
    }
}