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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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


        // -----------------------------------------------------
        // FIND NEEDS ASSESSMENT
        // -----------------------------------------------------

        NeedsAssessment needsAssessment =
                needsAssessmentService
                        .getNeedsAssessmentById(
                                needsAssessmentId
                        );


        if (needsAssessment == null) {

            return "redirect:/relief-coordinator/dashboard";
        }


        // -----------------------------------------------------
        // GET VICTIM
        // -----------------------------------------------------

        Victim victim =
                needsAssessment.getVictim();


        if (victim == null) {

            return "redirect:/relief-coordinator/dashboard";
        }


        // -----------------------------------------------------
        // REFRESH VICTIM
        // -----------------------------------------------------

        if (victim.getId() != null) {

            victim =
                    victimService
                            .getVictimById(
                                    victim.getId()
                            );
        }


        // =====================================================
        // FIND EXISTING RELIEF REQUIREMENT
        // =====================================================

        var existingRequirement =
                reliefRequirementService
                        .getByNeedsAssessment(
                                needsAssessment
                        );


        ReliefRequirement reliefRequirement;


        Set<String> selectedReliefItems =
                new HashSet<>();


        // =====================================================
        // EXISTING REQUIREMENT
        // =====================================================

        if (existingRequirement.isPresent()) {

            reliefRequirement =
                    existingRequirement.get();


            // -------------------------------------------------
            // CONNECT CURRENT NEEDS ASSESSMENT
            // -------------------------------------------------

            reliefRequirement.setNeedsAssessment(
                    needsAssessment
            );


            // -------------------------------------------------
            // CONNECT CURRENT VICTIM
            // -------------------------------------------------

            reliefRequirement.setVictim(
                    victim
            );


            // -------------------------------------------------
            // LOAD EXISTING RELIEF ITEMS
            // -------------------------------------------------

            if (
                    reliefRequirement.getReliefItems() != null
                            &&
                            !reliefRequirement
                                    .getReliefItems()
                                    .trim()
                                    .isEmpty()
            ) {

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


            // -------------------------------------------------
            // NORMALIZE EXISTING STATUS
            // -------------------------------------------------

            String existingStatus =
                    reliefRequirement.getStatus();


            if (existingStatus == null
                    || existingStatus.trim().isEmpty()) {

                reliefRequirement.setStatus(
                        "PENDING"
                );

            } else {

                existingStatus =
                        existingStatus
                                .trim()
                                .toUpperCase();


                /*
                 * RC page only supports:
                 *
                 * PENDING
                 * APPROVED
                 *
                 * If an old value such as
                 * "Pending" or "Approved" exists,
                 * convert it to uppercase.
                 */

                if ("APPROVED".equals(existingStatus)) {

                    reliefRequirement.setStatus(
                            "APPROVED"
                    );

                } else {

                    reliefRequirement.setStatus(
                            "PENDING"
                    );
                }
            }

        }


        // =====================================================
        // NEW REQUIREMENT
        // =====================================================

        else {

            reliefRequirement =
                    new ReliefRequirement();


            reliefRequirement.setNeedsAssessment(
                    needsAssessment
            );


            reliefRequirement.setVictim(
                    victim
            );


            /*
             * New relief requirements start as PENDING.
             */

            reliefRequirement.setStatus(
                    "PENDING"
            );
        }


        // =====================================================
        // MANDATORY RELIEF ITEMS
        // =====================================================

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


        // =====================================================
        // SEND DATA TO THYMELEAF
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


        // =====================================================
        // OPEN PAGE
        // =====================================================

        return "relief-coordinator/relief-requirement";
    }


    // =========================================================
    // SAVE RELIEF REQUIREMENT
    // =========================================================

    @PostMapping("/save")
    public String saveReliefRequirement(

            @RequestParam(
                    name = "needsAssessmentId"
            )
            Long needsAssessmentId,


            @RequestParam(
                    name = "reliefItems",
                    required = false
            )
            List<String> reliefItems,


            @RequestParam(
                    name = "victimMessage",
                    required = false
            )
            String victimMessage,


            @RequestParam(
                    name = "status"
            )
            String status,


            RedirectAttributes redirectAttributes) {


        // =====================================================
        // FIND NEEDS ASSESSMENT
        // =====================================================

        NeedsAssessment needsAssessment =
                needsAssessmentService
                        .getNeedsAssessmentById(
                                needsAssessmentId
                        );


        if (needsAssessment == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Needs Assessment could not be found."
            );

            return "redirect:/relief-coordinator/dashboard";
        }


        // =====================================================
        // GET VICTIM
        // =====================================================

        Victim victim =
                needsAssessment.getVictim();


        if (victim == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Victim information could not be found."
            );

            return "redirect:/relief-coordinator/dashboard";
        }


        // =====================================================
        // REFRESH VICTIM
        // =====================================================

        if (victim.getId() != null) {

            victim =
                    victimService
                            .getVictimById(
                                    victim.getId()
                            );
        }


        // =====================================================
        // FIND EXISTING REQUIREMENT
        // =====================================================

        var existingRequirement =
                reliefRequirementService
                        .getByNeedsAssessment(
                                needsAssessment
                        );


        ReliefRequirement reliefRequirement;


        // =====================================================
        // UPDATE EXISTING REQUIREMENT
        // =====================================================

        if (existingRequirement.isPresent()) {

            reliefRequirement =
                    existingRequirement.get();

        }


        // =====================================================
        // CREATE NEW REQUIREMENT
        // =====================================================

        else {

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
        // CONNECT NEEDS ASSESSMENT
        // =====================================================

        reliefRequirement.setNeedsAssessment(
                needsAssessment
        );


        // =====================================================
        // CONNECT VICTIM
        // =====================================================

        reliefRequirement.setVictim(
                victim
        );


        // =====================================================
        // RECIPIENT NAME
        // =====================================================

        String recipientName =
                victim.getFullName();


        if (
                recipientName == null
                        ||
                        recipientName.trim().isEmpty()
        ) {

            recipientName =
                    "Unknown Recipient";
        }


        reliefRequirement.setRecipientName(
                recipientName.trim()
        );


        // =====================================================
        // SHELTER INFORMATION
        // =====================================================

        String shelterName =
                "Not Assigned";


        String affectedArea =
                victim.getAddress();


        if (victim.getShelter() != null) {


            // -------------------------------------------------
            // SHELTER NAME
            // -------------------------------------------------

            if (
                    victim.getShelter()
                            .getShelterName() != null
                            &&
                            !victim.getShelter()
                                    .getShelterName()
                                    .trim()
                                    .isEmpty()
            ) {

                shelterName =
                        victim.getShelter()
                                .getShelterName()
                                .trim();
            }


            // -------------------------------------------------
            // SHELTER LOCATION
            // -------------------------------------------------

            if (
                    victim.getShelter()
                            .getLocation() != null
                            &&
                            !victim.getShelter()
                                    .getLocation()
                                    .trim()
                                    .isEmpty()
            ) {

                affectedArea =
                        victim.getShelter()
                                .getLocation()
                                .trim();
            }
        }


        // -----------------------------------------------------
        // SAFETY CHECK FOR AFFECTED AREA
        // -----------------------------------------------------

        if (
                affectedArea == null
                        ||
                        affectedArea.trim().isEmpty()
        ) {

            affectedArea =
                    "Not Specified";
        }


        // =====================================================
        // SAVE SHELTER
        // =====================================================

        reliefRequirement.setShelter(
                shelterName
        );


        // =====================================================
        // SAVE AFFECTED AREA
        // =====================================================

        reliefRequirement.setAffectedArea(
                affectedArea
        );


        // =====================================================
        // CREATE SAFE RELIEF ITEM LIST
        // =====================================================

        if (reliefItems == null) {

            reliefItems =
                    new ArrayList<>();
        }


        // =====================================================
        // ALWAYS ADD DRIED FOOD BAG
        // =====================================================

        if (
                !reliefItems.contains(
                        "Dried Food Bag"
                )
        ) {

            reliefItems.add(
                    "Dried Food Bag"
            );
        }


        // =====================================================
        // ALWAYS ADD 10 KG RICE BAG
        // =====================================================

        if (
                !reliefItems.contains(
                        "10 kg Rice Bag"
                )
        ) {

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

        /*
         * THIS IS THE IMPORTANT PART.
         *
         * The value coming from the HTML can be:
         *
         * Pending
         * pending
         * PENDING
         *
         * or
         *
         * Approved
         * approved
         * APPROVED
         *
         * We convert everything to uppercase before
         * saving to MySQL.
         */

        String finalStatus;


        if (
                status != null
                        &&
                        !status.trim().isEmpty()
        ) {

            String submittedStatus =
                    status
                            .trim()
                            .toUpperCase();


            if (
                    "APPROVED".equals(
                            submittedStatus
                    )
            ) {

                finalStatus =
                        "APPROVED";

            } else {

                finalStatus =
                        "PENDING";
            }

        } else {

            finalStatus =
                    "PENDING";
        }


        /*
         * Save the final normalized status.
         */

        reliefRequirement.setStatus(
                finalStatus
        );


        // =====================================================
        // MARK VICTIM AS NOTIFIED
        // =====================================================

        reliefRequirement.setVictimNotified(
                true
        );


        // =====================================================
        // SAVE TO DATABASE
        // =====================================================

        ReliefRequirement savedRequirement =
                reliefRequirementService
                        .createReliefRequirement(
                                reliefRequirement
                        );


        // =====================================================
        // VERIFY STATUS WAS SAVED
        // =====================================================

        if (
                savedRequirement == null
                        ||
                        savedRequirement.getStatus() == null
        ) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Relief requirement could not be saved."
            );

            return "redirect:/relief-coordinator/dashboard";
        }


        // =====================================================
        // SUCCESS MESSAGE
        // =====================================================

        redirectAttributes.addFlashAttribute(
                "success",
                "Relief requirement saved successfully with status: "
                        + savedRequirement.getStatus()
        );


        // =====================================================
        // RETURN TO DASHBOARD
        // =====================================================

        return "redirect:/relief-coordinator/dashboard";
    }
}