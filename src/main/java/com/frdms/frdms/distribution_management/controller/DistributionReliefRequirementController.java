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

import java.util.List;

@Controller
@RequestMapping("/distribution/requirements")
public class DistributionReliefRequirementController {

    private final ReliefRequirementService reliefRequirementService;

    private final AffectedFamilyInformationService
            affectedFamilyInformationService;

    public DistributionReliefRequirementController(
            ReliefRequirementService reliefRequirementService,
            AffectedFamilyInformationService
                    affectedFamilyInformationService) {

        this.reliefRequirementService =
                reliefRequirementService;

        this.affectedFamilyInformationService =
                affectedFamilyInformationService;
    }

    @GetMapping
    public String showReceivedRequirements(Model model) {

        List<ReliefRequirement> requirements =
                reliefRequirementService
                        .getApprovedRequirements();

        model.addAttribute(
                "requirements",
                requirements
        );

        return "distribution/requirements";
    }


    @GetMapping("/{id}")
    public String showRequirementDetails(
            @PathVariable Long id,
            Model model) {

        ReliefRequirement requirement =
                reliefRequirementService
                        .getRequirementById(id);

        model.addAttribute(
                "requirement",
                requirement
        );


        /*
         * Get the victim connected to this
         * relief requirement.
         */
        Victim victim =
                requirement.getVictim();


        /*
         * Find the affected family information
         * belonging to this victim.
         */
        AffectedFamilyInformation familyInformation =
                null;

        if (victim != null) {

            familyInformation =
                    affectedFamilyInformationService
                            .getByVictim(victim)
                            .orElse(null);
        }


        /*
         * Send family information to
         * requirement-details.html
         */
        model.addAttribute(
                "familyInformation",
                familyInformation
        );


        return "distribution/requirement-details";
    }
}