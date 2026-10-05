package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.service.ReliefRequirementService;
import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.frdms.frdms.distribution_management.service.ReliefPackageService;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/distribution/requirements")
public class DistributionReliefRequirementController {
    private final ReliefRequirementService reliefRequirementService;

    private final AffectedFamilyInformationService
            affectedFamilyInformationService;
    private final ReliefPackageService reliefPackageService;

    public DistributionReliefRequirementController(
            ReliefRequirementService reliefRequirementService,
            AffectedFamilyInformationService affectedFamilyInformationService,
            ReliefPackageService reliefPackageService) {

        this.reliefRequirementService =
                reliefRequirementService;

        this.affectedFamilyInformationService =
                affectedFamilyInformationService;
        this.reliefPackageService = reliefPackageService;
    }

    // =========================================================
    // SHOW APPROVED RELIEF REQUIREMENTS
    // =========================================================

    /**
     * Displays only relief requirements that have been
     * APPROVED by the Relief Coordinator.
     *
     * PENDING requirements are not displayed.
     *
     * READY FOR DISTRIBUTION requirements are also not
     * displayed here because the Distribution Officer must
     * first receive the approved requirement and prepare
     * the package.
     */
    @GetMapping
    public String showReceivedRequirements(Model model) {

        List<ReliefRequirement> allRequirements =
                reliefRequirementService
                        .getAllRequirements();

        /*
         * Only APPROVED requirements are shown.
         */
        List<ReliefRequirement> approvedRequirements =
                allRequirements
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

        model.addAttribute(
                "requirements",
                approvedRequirements
        );

        Set<Long> packageCreatedRequirementIds = new HashSet<>();

        for (ReliefRequirement requirement : approvedRequirements) {
            if (reliefPackageService.existsByRequirementId(requirement.getId())) {
                packageCreatedRequirementIds.add(requirement.getId());
            }
        }

        model.addAttribute(
                "packageCreatedRequirementIds",
                packageCreatedRequirementIds
        );

        return "distribution/requirements";
    }


    // =========================================================
    // VIEW REQUIREMENT DETAILS
    // =========================================================

    /**
     * Opens the complete information of one approved
     * relief requirement.
     *
     * The Distribution Officer can see:
     *
     * 1. Relief requirement information
     * 2. Quantities decided by Relief Coordinator
     * 3. Victim personal information
     * 4. Family information
     * 5. Needs assessment
     * 6. Delivery location
     * 7. Assigned shelter information
     * 8. Additional victim request/message
     */
    @GetMapping("/{id}")
    public String showRequirementDetails(
            @PathVariable Long id,
            Model model) {

        ReliefRequirement requirement;

        try {

            requirement =
                    reliefRequirementService
                            .getRequirementById(id);

        } catch (Exception e) {

            return "redirect:/distribution/requirements";
        }


        /*
         * Safety check.
         *
         * The Distribution Officer should only be able
         * to view APPROVED requirements through this page.
         */
        if (requirement.getStatus() == null
                || !"APPROVED".equalsIgnoreCase(
                requirement.getStatus().trim()
        )) {

            return "redirect:/distribution/requirements";
        }


        // =====================================================
        // ADD RELIEF REQUIREMENT
        // =====================================================

        model.addAttribute(
                "requirement",
                requirement
        );
        boolean packageExists =
                reliefPackageService.existsByRequirementId(requirement.getId());

        model.addAttribute("packageExists", packageExists);


        // =====================================================
        // PREPARE ADDITIONAL ITEMS REQUESTED BY VICTIM
        // =====================================================

        /*
         * The Relief Coordinator saves the victim's additional
         * request inside the reliefItems field using:
         *
         * "Additional Items Requested: item"
         *
         * Extract those additional requests separately so that
         * the Distribution page can display them.
         */
        List<String> additionalItems = new ArrayList<>();

        String reliefItems = requirement.getReliefItems();

        if (reliefItems != null && !reliefItems.trim().isEmpty()) {

            String[] items = reliefItems.split("\\|");

            for (String item : items) {

                String trimmedItem = item.trim();

                if (trimmedItem
                        .toLowerCase()
                        .startsWith("additional items requested:")) {

                    String additionalItem =
                            trimmedItem.substring(
                                    "Additional Items Requested:".length()
                            ).trim();

                    if (!additionalItem.isEmpty()) {
                        additionalItems.add(additionalItem);
                    }
                }
            }
        }

        model.addAttribute(
                "additionalItems",
                additionalItems
        );


        // =====================================================
        // GET VICTIM
        // =====================================================

        Victim victim =
                requirement.getVictim();

        model.addAttribute(
                "victim",
                victim
        );

// =====================================================
// GET DISASTER
// =====================================================

        String disasterName = null;

        if (victim != null && victim.getDisaster() != null) {
            disasterName = victim.getDisaster().getEventName();
        }

        model.addAttribute(
                "disasterName",
                disasterName
        );
        // =====================================================
        // GET FAMILY INFORMATION
        // =====================================================

        AffectedFamilyInformation familyInformation =
                null;

        if (victim != null) {

            familyInformation =
                    affectedFamilyInformationService
                            .getByVictim(victim)
                            .orElse(null);
        }

        model.addAttribute(
                "familyInformation",
                familyInformation
        );


        // =====================================================
        // GET NEEDS ASSESSMENT
        // =====================================================

        /*
         * ReliefRequirement already has the connection to
         * the NeedsAssessment.
         *
         * Adding it separately makes the Thymeleaf page
         * easier to read.
         */
        model.addAttribute(
                "needsAssessment",
                requirement.getNeedsAssessment()
        );


        // =====================================================
        // RETURN DETAILS PAGE
        // =====================================================

        return "distribution/requirement-details";
    }
}