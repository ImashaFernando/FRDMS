package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.NeedsAssessmentService;
import com.frdms.frdms.victim_management.service.ReliefRequirementService;
import com.frdms.frdms.victim_management.service.VictimService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/relief-requirements")
public class ReliefRequirementController {

    private final ReliefRequirementService reliefRequirementService;
    private final NeedsAssessmentService needsAssessmentService;
    private final VictimService victimService;

    public ReliefRequirementController(
            ReliefRequirementService reliefRequirementService,
            NeedsAssessmentService needsAssessmentService,
            VictimService victimService) {

        this.reliefRequirementService = reliefRequirementService;
        this.needsAssessmentService = needsAssessmentService;
        this.victimService = victimService;
    }


    // =========================================================
    // OPEN RELIEF REQUIREMENT FORM
    // =========================================================

    @GetMapping("/new/{needsAssessmentId}")
    public String showReliefRequirementForm(
            @PathVariable Long needsAssessmentId,
            Model model) {

        NeedsAssessment needsAssessment =
                needsAssessmentService.getNeedsAssessmentById(
                        needsAssessmentId
                );

        if (needsAssessment == null) {
            return "redirect:/relief-coordinator/needs-assessments";
        }

        Victim victim = needsAssessment.getVictim();

        /*
         * Check whether a relief requirement has already
         * been created for this needs assessment.
         */
        var existingRequirement =
                reliefRequirementService.getByNeedsAssessment(
                        needsAssessment
                );

        ReliefRequirement reliefRequirement;

        Set<String> selectedReliefItems = new HashSet<>();

        if (existingRequirement.isPresent()) {

            reliefRequirement = existingRequirement.get();

            /*
             * Convert the previously saved String back into
             * individual checklist items.
             */
            if (reliefRequirement.getReliefItems() != null
                    && !reliefRequirement.getReliefItems().trim().isEmpty()) {

                selectedReliefItems.addAll(
                        Arrays.asList(
                                reliefRequirement
                                        .getReliefItems()
                                        .split("\\s*\\|\\s*")
                        )
                );
            }

        } else {

            reliefRequirement = new ReliefRequirement();

            reliefRequirement.setNeedsAssessment(
                    needsAssessment
            );

            reliefRequirement.setVictim(victim);

            /*
             * These two items are mandatory for every victim.
             */
            selectedReliefItems.add("Dried Food Bag");
            selectedReliefItems.add("10 kg Rice Bag");
        }

        model.addAttribute(
                "reliefRequirement",
                reliefRequirement
        );

        model.addAttribute(
                "needsAssessment",
                needsAssessment
        );

        model.addAttribute(
                "victim",
                victim
        );

        model.addAttribute(
                "selectedReliefItems",
                selectedReliefItems
        );

        return "relief-coordinator/relief-requirement";
    }


    // =========================================================
    // SAVE RELIEF REQUIREMENT
    // =========================================================

    @PostMapping("/save")
    public String saveReliefRequirement(

            @RequestParam Long needsAssessmentId,

            @RequestParam(
                    value = "reliefItems",
                    required = false
            )
            List<String> reliefItems,

            @RequestParam(
                    value = "victimMessage",
                    required = false
            )
            String victimMessage,

            @RequestParam(
                    value = "status",
                    required = false
            )
            String status) {


        NeedsAssessment needsAssessment =
                needsAssessmentService.getNeedsAssessmentById(
                        needsAssessmentId
                );

        if (needsAssessment == null) {
            return "redirect:/relief-coordinator/needs-assessments";
        }

        Victim victim = needsAssessment.getVictim();


        // -----------------------------------------------------
        // FIND EXISTING REQUIREMENT
        // -----------------------------------------------------

        var existingRequirement =
                reliefRequirementService.getByNeedsAssessment(
                        needsAssessment
                );

        ReliefRequirement reliefRequirement;


        if (existingRequirement.isPresent()) {

            /*
             * UPDATE existing requirement instead of creating
             * a duplicate record.
             */
            reliefRequirement =
                    existingRequirement.get();

        } else {

            /*
             * CREATE new requirement.
             */
            reliefRequirement =
                    new ReliefRequirement();

            reliefRequirement.setNeedsAssessment(
                    needsAssessment
            );

            reliefRequirement.setVictim(
                    victim
            );
        }


        // -----------------------------------------------------
        // MANDATORY ITEMS
        // -----------------------------------------------------

        /*
         * Every victim must receive these two items.
         */
        if (reliefItems == null) {
            reliefItems = new java.util.ArrayList<>();
        }

        if (!reliefItems.contains("Dried Food Bag")) {
            reliefItems.add("Dried Food Bag");
        }

        if (!reliefItems.contains("10 kg Rice Bag")) {
            reliefItems.add("10 kg Rice Bag");
        }


        // -----------------------------------------------------
        // SAVE ITEMS
        // -----------------------------------------------------

        String savedReliefItems =
                String.join(" | ", reliefItems);

        reliefRequirement.setReliefItems(
                savedReliefItems
        );


        // -----------------------------------------------------
        // SAVE MESSAGE
        // -----------------------------------------------------

        if (victimMessage != null) {

            reliefRequirement.setVictimMessage(
                    victimMessage.trim()
            );

        } else {

            reliefRequirement.setVictimMessage("");
        }


        // -----------------------------------------------------
        // SAVE STATUS
        // -----------------------------------------------------

        if (status != null
                && !status.trim().isEmpty()) {

            reliefRequirement.setStatus(
                    status
            );

        } else {

            reliefRequirement.setStatus(
                    "Pending"
            );
        }


        // -----------------------------------------------------
        // DATABASE SAVE
        // -----------------------------------------------------

        reliefRequirementService.createReliefRequirement(
                reliefRequirement
        );


        // -----------------------------------------------------
        // RETURN TO NEEDS ASSESSMENTS
        // -----------------------------------------------------

        return "redirect:/relief-coordinator/needs-assessments";
    }
}