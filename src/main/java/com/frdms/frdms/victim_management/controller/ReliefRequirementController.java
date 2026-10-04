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


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

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


        // -----------------------------------------------------
        // SAFETY CHECK
        // -----------------------------------------------------

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
        // CHECK EXISTING RELIEF REQUIREMENT
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
            // UPDATE VICTIM / ASSESSMENT REFERENCES
            // -------------------------------------------------

            reliefRequirement.setNeedsAssessment(
                    needsAssessment
            );

            reliefRequirement.setVictim(
                    victim
            );


            // -------------------------------------------------
            // LOAD EXISTING RELIEF ITEMS
            // -------------------------------------------------

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


            // -------------------------------------------------
            // REMOVE OLD ITEMS THAT ARE NO LONGER USED
            // -------------------------------------------------

            selectedReliefItems.remove(
                    "10 kg Rice Bag"
            );

            selectedReliefItems.remove(
                    "Torch / Emergency Light"
            );

            selectedReliefItems.remove(
                    "Cooking Equipment"
            );
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
        }


        // =====================================================
        // IMPORTANT
        // =====================================================
        /*
         * The Relief Coordinator should select the assistance
         * according to the victim's Needs Assessment.
         *
         * We NO LONGER automatically add:
         *
         * - 10 kg Rice Bag
         * - Torch / Emergency Light
         * - Cooking Equipment
         *
         * Dried Food Bag is also NOT forced here.
         *
         * The coordinator selects the actual assistance
         * from the HTML form.
         */


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


        // =====================================================
        // OPEN RELIEF REQUIREMENT PAGE
        // =====================================================

        return "relief-coordinator/relief-requirement";
    }


    // =========================================================
    // SAVE RELIEF REQUIREMENT
    // =========================================================

    @PostMapping("/save")
    public String saveReliefRequirement(

            @RequestParam Long needsAssessmentId,


            // -------------------------------------------------
            // SELECTED RELIEF ITEMS
            // -------------------------------------------------

            @RequestParam(
                    value = "reliefItems",
                    required = false
            )
            List<String> reliefItems,


            // -------------------------------------------------
            // ADDITIONAL ITEMS
            // -------------------------------------------------
            /*
             * This comes from the new text box in the HTML.
             *
             * Example:
             *
             * Rice 25kg
             * Baby formula
             * Mosquito nets
             *
             * It will be stored together with the selected
             * relief items.
             */

            @RequestParam(
                    value = "additionalItems",
                    required = false
            )
            String additionalItems,


            // -------------------------------------------------
            // VICTIM MESSAGE
            // -------------------------------------------------

            @RequestParam(
                    value = "victimMessage",
                    required = false
            )
            String victimMessage,


            // -------------------------------------------------
            // STATUS
            // -------------------------------------------------

            @RequestParam(
                    value = "status",
                    required = false
            )
            String status,


            // -------------------------------------------------
            // SEND TO DISTRIBUTION
            // -------------------------------------------------

            @RequestParam(
                    value = "sendToDistribution",
                    required = false,
                    defaultValue = "false"
            )
            boolean sendToDistribution,


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


        if (recipientName == null
                || recipientName.trim().isEmpty()) {

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


        // -----------------------------------------------------
        // IF VICTIM HAS A SHELTER
        // -----------------------------------------------------

        if (victim.getShelter() != null) {

            // -------------------------------------------------
            // SHELTER NAME
            // -------------------------------------------------

            if (victim.getShelter()
                    .getShelterName() != null
                    && !victim.getShelter()
                    .getShelterName()
                    .trim()
                    .isEmpty()) {

                shelterName =
                        victim.getShelter()
                                .getShelterName()
                                .trim();
            }


            // -------------------------------------------------
            // SHELTER LOCATION
            // -------------------------------------------------

            if (victim.getShelter()
                    .getLocation() != null
                    && !victim.getShelter()
                    .getLocation()
                    .trim()
                    .isEmpty()) {

                affectedArea =
                        victim.getShelter()
                                .getLocation()
                                .trim();
            }
        }


        // -----------------------------------------------------
        // SAFETY CHECK
        // -----------------------------------------------------

        if (affectedArea == null
                || affectedArea.trim().isEmpty()) {

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
        // REMOVE OLD / DISCONTINUED ITEMS
        // =====================================================

        /*
         * These items are no longer part of the
         * Relief Coordinator assistance form.
         */

        reliefItems.remove(
                "10 kg Rice Bag"
        );

        reliefItems.remove(
                "Torch / Emergency Light"
        );

        reliefItems.remove(
                "Cooking Equipment"
        );


        // =====================================================
        // CLEAN EMPTY VALUES
        // =====================================================

        reliefItems.removeIf(
                item ->
                        item == null
                                || item.trim().isEmpty()
        );


        // =====================================================
        // ADD ADDITIONAL ITEMS
        // =====================================================

        /*
         * Additional items are entered manually by the
         * Relief Coordinator according to the victim's
         * submitted Needs Assessment.
         *
         * They are added to the same reliefItems field so
         * Distribution Management can see them later.
         */

        if (additionalItems != null
                && !additionalItems.trim().isEmpty()) {

            String[] additionalItemList =
                    additionalItems
                            .split("[,\\n]");


            for (String item :
                    additionalItemList) {

                if (item != null
                        && !item.trim().isEmpty()) {

                    String cleanedItem =
                            item.trim();


                    // Prevent discontinued items
                    // from being saved through
                    // the additional-items box.

                    if (!cleanedItem.equalsIgnoreCase(
                            "10 kg Rice Bag")
                            && !cleanedItem.equalsIgnoreCase(
                            "Torch / Emergency Light")
                            && !cleanedItem.equalsIgnoreCase(
                            "Cooking Equipment")) {

                        reliefItems.add(
                                cleanedItem
                        );
                    }
                }
            }
        }


        // =====================================================
        // REMOVE DUPLICATE ITEMS
        // =====================================================

        List<String> cleanedReliefItems =
                new ArrayList<>();


        for (String item :
                reliefItems) {

            if (item != null
                    && !item.trim().isEmpty()
                    && !cleanedReliefItems.contains(
                    item.trim())) {

                cleanedReliefItems.add(
                        item.trim()
                );
            }
        }


        // =====================================================
        // SAVE RELIEF ITEMS
        // =====================================================

        String savedReliefItems =
                String.join(
                        " | ",
                        cleanedReliefItems
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

        if (sendToDistribution) {

            /*
             * When the RC clicks:
             *
             * Save & Send to Victim + Distribution
             *
             * the requirement becomes ready for
             * Distribution Management.
             */

            reliefRequirement.setStatus(
                    "READY FOR DISTRIBUTION"
            );

        } else if (status != null
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
        // MARK VICTIM AS NOTIFIED
        // =====================================================

        /*
         * The saved requirement is now available for
         * the Victim Dashboard.
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
        // RESULT MESSAGE
        // =====================================================

        if (sendToDistribution) {

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Relief requirement saved and sent to the Victim and Distribution Management successfully."
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Relief requirement saved successfully."
            );
        }


        // =====================================================
        // AFTER SAVE
        // =====================================================

        /*
         * Keep the existing Relief Coordinator flow.
         *
         * Distribution Management reads the same
         * ReliefRequirement record from the database.
         */

        return "redirect:/relief-coordinator/dashboard";
    }
}