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

import java.util.ArrayList;
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

        this.reliefRequirementService =
                reliefRequirementService;

        this.needsAssessmentService =
                needsAssessmentService;

        this.victimService =
                victimService;
    }



    // =========================================================
    // OPEN RELIEF REQUIREMENT FORM
    // =========================================================

    @GetMapping("/new/{needsAssessmentId}")
    public String showReliefRequirementForm(
            @PathVariable Long needsAssessmentId,
            Model model) {


        /*
         * Find the Needs Assessment.
         */
        NeedsAssessment needsAssessment =
                needsAssessmentService
                        .getNeedsAssessmentById(
                                needsAssessmentId
                        );


        /*
         * If assessment doesn't exist,
         * return to assessment list.
         */
        if (needsAssessment == null) {

            return "redirect:/relief-coordinator/needs-assessments";
        }


        /*
         * Get the victim connected to the assessment.
         */
        Victim victim =
                needsAssessment.getVictim();



        // =====================================================
        // CHECK FOR EXISTING RELIEF REQUIREMENT
        // =====================================================

        var existingRequirement =
                reliefRequirementService
                        .getByNeedsAssessment(
                                needsAssessment
                        );


        ReliefRequirement reliefRequirement;



        /*
         * Holds the relief items that were previously
         * selected and saved.
         */
        Set<String> selectedReliefItems =
                new HashSet<>();



        // =====================================================
        // EXISTING RECORD
        // =====================================================

        if (existingRequirement.isPresent()) {


            /*
             * Load the existing requirement.
             *
             * This means the coordinator can return later
             * and update the same requirement.
             */
            reliefRequirement =
                    existingRequirement.get();


            /*
             * Convert the saved String back into individual
             * checklist items.
             *
             * Example:
             *
             * Dried Food Bag | 10 kg Rice Bag | Drinking Water
             *
             * becomes:
             *
             * Dried Food Bag
             * 10 kg Rice Bag
             * Drinking Water
             */
            if (reliefRequirement.getReliefItems() != null
                    && !reliefRequirement
                    .getReliefItems()
                    .trim()
                    .isEmpty()) {


                selectedReliefItems.addAll(
                        Arrays.asList(
                                reliefRequirement
                                        .getReliefItems()
                                        .split(
                                                "\\s*\\|\\s*"
                                        )
                        )
                );
            }


        } else {


            // =================================================
            // NEW RECORD
            // =================================================

            reliefRequirement =
                    new ReliefRequirement();


            reliefRequirement.setNeedsAssessment(
                    needsAssessment
            );


            reliefRequirement.setVictim(
                    victim
            );


            /*
             * Every victim automatically receives:
             *
             * 1. Dried Food Bag
             * 2. 10 kg Rice Bag
             */
            selectedReliefItems.add(
                    "Dried Food Bag"
            );


            selectedReliefItems.add(
                    "10 kg Rice Bag"
            );
        }



        // =====================================================
        // SEND DATA TO HTML
        // =====================================================

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



        // =====================================================
        // FIND NEEDS ASSESSMENT
        // =====================================================

        NeedsAssessment needsAssessment =
                needsAssessmentService
                        .getNeedsAssessmentById(
                                needsAssessmentId
                        );


        if (needsAssessment == null) {

            return "redirect:/relief-coordinator/needs-assessments";
        }


        /*
         * Get connected victim.
         */
        Victim victim =
                needsAssessment.getVictim();



        // =====================================================
        // FIND EXISTING REQUIREMENT
        // =====================================================

        var existingRequirement =
                reliefRequirementService
                        .getByNeedsAssessment(
                                needsAssessment
                        );


        ReliefRequirement reliefRequirement;



        if (existingRequirement.isPresent()) {


            /*
             * Update existing record.
             *
             * This prevents duplicate relief requirements
             * for the same Needs Assessment.
             */
            reliefRequirement =
                    existingRequirement.get();


        } else {


            /*
             * Create new record.
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



        // =====================================================
        // CREATE A SAFE ITEM LIST
        // =====================================================

        if (reliefItems == null) {

            reliefItems =
                    new ArrayList<>();
        }


        /*
         * Make sure the two standard relief items
         * are ALWAYS included.
         */
        if (!reliefItems.contains(
                "Dried Food Bag")) {

            reliefItems.add(
                    "Dried Food Bag"
            );
        }


        if (!reliefItems.contains(
                "10 kg Rice Bag")) {

            reliefItems.add(
                    "10 kg Rice Bag"
            );
        }



        // =====================================================
        // SAVE RELIEF ITEMS
        // =====================================================

        String savedReliefItems =
                String.join(
                        " | ",
                        reliefItems
                );


        reliefRequirement.setReliefItems(
                savedReliefItems
        );



        // =====================================================
        // SAVE VICTIM MESSAGE
        // =====================================================

        if (victimMessage != null) {

            reliefRequirement.setVictimMessage(
                    victimMessage.trim()
            );

        } else {

            reliefRequirement.setVictimMessage(
                    ""
            );
        }



        // =====================================================
        // SAVE STATUS
        // =====================================================

        if (status != null
                && !status.trim().isEmpty()) {

            reliefRequirement.setStatus(
                    status.trim()
            );

        } else {

            reliefRequirement.setStatus(
                    "Pending"
            );
        }



        // =====================================================
        // CONFIRM VICTIM NOTIFICATION
        // =====================================================

        /*
         * Saving the confirmed Relief Requirement means
         * the approved/confirmed relief information is now
         * recorded as sent/confirmed for the victim.
         *
         * true  = Sent to Victim
         */
        reliefRequirement.setVictimNotified(
                true
        );



        // =====================================================
        // SAVE TO DATABASE
        // =====================================================

        reliefRequirementService
                .createReliefRequirement(
                        reliefRequirement
                );



        // =====================================================
        // RETURN TO NEEDS ASSESSMENTS
        // =====================================================

        return "redirect:/relief-coordinator/needs-assessments";
    }
}