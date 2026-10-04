package com.frdms.frdms.distribution_management.service;

import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.repository.ReliefRequirementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReliefRequirementService {

    private final ReliefRequirementRepository reliefRequirementRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReliefRequirementService(
            ReliefRequirementRepository reliefRequirementRepository) {

        this.reliefRequirementRepository =
                reliefRequirementRepository;
    }


    // =========================================================
    // GET APPROVED REQUIREMENTS
    // =========================================================

    /**
     * Gets relief requirements that have been
     * approved by the Relief Coordinator and sent
     * to Distribution Management.
     *
     * These requirements are read directly from the
     * Victim Management relief_requirements table.
     *
     * IMPORTANT:
     *
     * The Relief Coordinator must select "Approved"
     * before sending the requirement to Distribution
     * Management.
     *
     * Therefore Distribution Management retrieves
     * requirements with the status:
     *
     * APPROVED
     */

    @Transactional(readOnly = true)
    public List<ReliefRequirement> getApprovedRequirements() {

        return reliefRequirementRepository
                .findAll()
                .stream()

                .filter(requirement ->
                        requirement.getStatus() != null
                                &&
                                "APPROVED".equalsIgnoreCase(
                                        requirement.getStatus().trim()
                                )
                )

                .sorted(
                        Comparator.comparing(
                                ReliefRequirement::getCreatedAt,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )

                .collect(Collectors.toList());
    }


    // =========================================================
    // GET ALL REQUIREMENTS
    // =========================================================

    /**
     * Get all relief requirements.
     *
     * Used when Distribution Management needs to
     * display all received requirements.
     */

    @Transactional(readOnly = true)
    public List<ReliefRequirement> getAllRequirements() {

        return reliefRequirementRepository
                .findAll()
                .stream()

                .sorted(
                        Comparator.comparing(
                                ReliefRequirement::getCreatedAt,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )

                .collect(Collectors.toList());
    }


    // =========================================================
    // GET ONE REQUIREMENT BY ID
    // =========================================================

    /**
     * Get one relief requirement by ID.
     */

    @Transactional(readOnly = true)
    public ReliefRequirement getRequirementById(
            Long id) {

        return reliefRequirementRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Relief requirement not found with ID: "
                                        + id
                        )
                );
    }


    // =========================================================
    // COUNT APPROVED REQUIREMENTS
    // =========================================================

    /**
     * Returns the number of approved relief
     * requirements currently waiting to be
     * processed by Distribution Management.
     */

    @Transactional(readOnly = true)
    public long getApprovedRequirementCount() {

        return getApprovedRequirements().size();
    }


    // =========================================================
    // GET LATEST APPROVED REQUIREMENT
    // =========================================================

    /**
     * Returns the latest approved requirement that
     * has been sent to Distribution Management.
     *
     * Used by the Distribution dashboard to display
     * the latest requirement date.
     */

    @Transactional(readOnly = true)
    public ReliefRequirement getLatestApprovedRequirement() {

        List<ReliefRequirement> requirements =
                getApprovedRequirements();


        if (requirements.isEmpty()) {

            return null;
        }


        return requirements.get(0);
    }
}