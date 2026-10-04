package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.entity.Victim;

import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;
import com.frdms.frdms.victim_management.service.NeedsAssessmentService;
import com.frdms.frdms.victim_management.service.ReliefRequirementService;
import com.frdms.frdms.victim_management.service.VictimService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/relief-requirements")
public class ReliefRequirementController {

    private final ReliefRequirementService reliefRequirementService;
    private final NeedsAssessmentService needsAssessmentService;
    private final VictimService victimService;
    private final AffectedFamilyInformationService affectedFamilyInformationService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReliefRequirementController(
            ReliefRequirementService reliefRequirementService,
            NeedsAssessmentService needsAssessmentService,
            VictimService victimService,
            AffectedFamilyInformationService affectedFamilyInformationService) {

        this.reliefRequirementService =
                reliefRequirementService;

        this.needsAssessmentService =
                needsAssessmentService;

        this.victimService =
                victimService;

        this.affectedFamilyInformationService =
                affectedFamilyInformationService;
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
        // GET FAMILY INFORMATION
        // =====================================================

        /*
         * Family information is used by the Relief Coordinator
         * to decide the correct quantities of relief items.
         *
         * The page can display:
         *
         * - Total family members
         * - Male members
         * - Female members
         * - Children below 12
         */

        AffectedFamilyInformation familyInformation =
                affectedFamilyInformationService
                        .getByVictim(victim)
                        .orElse(null);


        // =====================================================
        // CHECK EXISTING RELIEF REQUIREMENT
        // =====================================================

        var existingRequirement =
                reliefRequirementService
                        .getByNeedsAssessment(
                                needsAssessment
                        );


        ReliefRequirement reliefRequirement;


        // =====================================================
        // EXISTING REQUIREMENT
        // =====================================================

        if (existingRequirement.isPresent()) {

            reliefRequirement =
                    existingRequirement.get();

            reliefRequirement.setNeedsAssessment(
                    needsAssessment
            );

            reliefRequirement.setVictim(
                    victim
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

            /*
             * Dried Food Bag is automatically included.
             */

            reliefRequirement.setDriedFoodBagQuantity(1);

            /*
             * The following five items are optional.
             */

            reliefRequirement.setDrinkingWaterQuantity(0);

            reliefRequirement.setMedicalSuppliesQuantity(0);

            reliefRequirement.setHygieneKitQuantity(0);

            reliefRequirement.setBabyCareItemsQuantity(0);

            reliefRequirement.setBeddingQuantity(0);
        }


        // =====================================================
        // CREATE CURRENT RELIEF ITEM DISPLAY
        // =====================================================

        List<String> selectedReliefItems =
                new ArrayList<>();


        if (reliefRequirement.getReliefItems() != null
                && !reliefRequirement
                .getReliefItems()
                .trim()
                .isEmpty()) {

            String[] items =
                    reliefRequirement
                            .getReliefItems()
                            .split("\\s*\\|\\s*");

            for (String item : items) {

                if (item != null
                        && !item.trim().isEmpty()) {

                    selectedReliefItems.add(
                            item.trim()
                    );
                }
            }
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
                "familyInformation",
                familyInformation
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
            // DRIED FOOD BAG
            // -------------------------------------------------

            @RequestParam(
                    value = "driedFoodBagQuantity",
                    required = false,
                    defaultValue = "1"
            )
            Integer driedFoodBagQuantity,


            // -------------------------------------------------
            // DRINKING WATER
            // -------------------------------------------------

            @RequestParam(
                    value = "drinkingWaterQuantity",
                    required = false,
                    defaultValue = "0"
            )
            Integer drinkingWaterQuantity,


            // -------------------------------------------------
            // MEDICAL SUPPLIES
            // -------------------------------------------------

            @RequestParam(
                    value = "medicalSuppliesQuantity",
                    required = false,
                    defaultValue = "0"
            )
            Integer medicalSuppliesQuantity,


            // -------------------------------------------------
            // HYGIENE KIT
            // -------------------------------------------------

            @RequestParam(
                    value = "hygieneKitQuantity",
                    required = false,
                    defaultValue = "0"
            )
            Integer hygieneKitQuantity,


            // -------------------------------------------------
            // BABY CARE ITEMS
            // -------------------------------------------------

            @RequestParam(
                    value = "babyCareItemsQuantity",
                    required = false,
                    defaultValue = "0"
            )
            Integer babyCareItemsQuantity,


            // -------------------------------------------------
            // BEDDING
            // -------------------------------------------------

            @RequestParam(
                    value = "beddingQuantity",
                    required = false,
                    defaultValue = "0"
            )
            Integer beddingQuantity,


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
        // NORMALIZE QUANTITIES
        // =====================================================

        /*
         * Dried Food Bag is automatically included.
         * Therefore the minimum quantity is 1.
         */

        if (driedFoodBagQuantity == null
                || driedFoodBagQuantity < 1) {

            driedFoodBagQuantity = 1;
        }


        if (drinkingWaterQuantity == null
                || drinkingWaterQuantity < 0) {

            drinkingWaterQuantity = 0;
        }


        if (medicalSuppliesQuantity == null
                || medicalSuppliesQuantity < 0) {

            medicalSuppliesQuantity = 0;
        }


        if (hygieneKitQuantity == null
                || hygieneKitQuantity < 0) {

            hygieneKitQuantity = 0;
        }


        if (babyCareItemsQuantity == null
                || babyCareItemsQuantity < 0) {

            babyCareItemsQuantity = 0;
        }


        if (beddingQuantity == null
                || beddingQuantity < 0) {

            beddingQuantity = 0;
        }


        // =====================================================
        // SAVE QUANTITIES
        // =====================================================

        reliefRequirement.setDriedFoodBagQuantity(
                driedFoodBagQuantity
        );


        reliefRequirement.setDrinkingWaterQuantity(
                drinkingWaterQuantity
        );


        reliefRequirement.setMedicalSuppliesQuantity(
                medicalSuppliesQuantity
        );


        reliefRequirement.setHygieneKitQuantity(
                hygieneKitQuantity
        );


        reliefRequirement.setBabyCareItemsQuantity(
                babyCareItemsQuantity
        );


        reliefRequirement.setBeddingQuantity(
                beddingQuantity
        );


        // =====================================================
        // CREATE RELIEF ITEM SUMMARY
        // =====================================================

        List<String> savedReliefItems =
                new ArrayList<>();


        /*
         * Dried Food Bag is always included.
         */

        savedReliefItems.add(
                "Dried Food Bag"
        );


        // -----------------------------------------------------
        // DRINKING WATER
        // -----------------------------------------------------

        if (drinkingWaterQuantity > 0) {

            savedReliefItems.add(
                    "Drinking Water ("
                            + drinkingWaterQuantity
                            + ")"
            );
        }


        // -----------------------------------------------------
        // MEDICAL SUPPLIES
        // -----------------------------------------------------

        if (medicalSuppliesQuantity > 0) {

            savedReliefItems.add(
                    "Medicine / Medical Supplies ("
                            + medicalSuppliesQuantity
                            + ")"
            );
        }


        // -----------------------------------------------------
        // HYGIENE KIT
        // -----------------------------------------------------

        if (hygieneKitQuantity > 0) {

            savedReliefItems.add(
                    "Hygiene Kit ("
                            + hygieneKitQuantity
                            + ")"
            );
        }


        // -----------------------------------------------------
        // BABY CARE ITEMS
        // -----------------------------------------------------

        if (babyCareItemsQuantity > 0) {

            savedReliefItems.add(
                    "Baby Care Items ("
                            + babyCareItemsQuantity
                            + ")"
            );
        }


        // -----------------------------------------------------
        // BEDDING
        // -----------------------------------------------------

        if (beddingQuantity > 0) {

            savedReliefItems.add(
                    "Bedding ("
                            + beddingQuantity
                            + ")"
            );
        }


        // =====================================================
        // SAVE RELIEF ITEM SUMMARY
        // =====================================================

        reliefRequirement.setReliefItems(
                String.join(
                        " | ",
                        savedReliefItems
                )
        );


        // =====================================================
        // HANDLE VICTIM'S ADDITIONAL NEEDS
        // =====================================================

        /*
         * The victim can enter additional items under
         * "Other Needs".
         *
         * The Relief Coordinator does NOT create a quantity
         * for these items.
         *
         * Instead:
         *
         * 1. The additional request is shown to the victim
         *    in the confirmation message.
         *
         * 2. The same information is saved inside the
         *    ReliefRequirement victimMessage.
         *
         * 3. Distribution Management can read the same
         *    ReliefRequirement and see the additional request.
         *
         * Example:
         *
         * Victim enters:
         *
         * "Baby diapers, mosquito nets"
         *
         * The message becomes:
         *
         * "Additional items requested by you:
         * Baby diapers, mosquito nets.
         * These additional items have also been communicated
         * to Distribution Management for further
         * consideration and fulfillment."
         *
         * If the victim enters "no", nothing is added.
         */

        String victimAdditionalNeeds =
                needsAssessment.getOtherNeeds();


        boolean hasAdditionalNeeds =
                victimAdditionalNeeds != null
                        && !victimAdditionalNeeds
                        .trim()
                        .isEmpty()
                        && !"no".equalsIgnoreCase(
                        victimAdditionalNeeds.trim()
                );


        // =====================================================
        // PREPARE BASE VICTIM MESSAGE
        // =====================================================

        String finalVictimMessage =
                victimMessage;


        if (finalVictimMessage == null) {

            finalVictimMessage =
                    "";
        }


        finalVictimMessage =
                finalVictimMessage.trim();


        // =====================================================
        // ADD ADDITIONAL ITEMS TO MESSAGE
        // =====================================================

        if (hasAdditionalNeeds) {

            // Save the victim's additional items inside reliefItems as well.
            // This allows Distribution Management to read the additional
            // request from the same ReliefRequirement record.
            savedReliefItems.add(
                    "Additional Items Requested: "
                            + victimAdditionalNeeds.trim()
            );

            reliefRequirement.setReliefItems(
                    String.join(
                            " | ",
                            savedReliefItems
                    )
            );

            String additionalItemsMessage =
                    "Additional items requested by you: "
                            + victimAdditionalNeeds.trim()
                            + ". These additional items have also been communicated to Distribution Management for further consideration and fulfillment.";


            /*
             * Prevent duplicate additional-item messages
             * when an existing requirement is updated.
             */

            if (!finalVictimMessage
                    .toLowerCase()
                    .contains(
                            "additional items requested by you:"
                    )) {

                if (!finalVictimMessage.isEmpty()) {

                    finalVictimMessage =
                            finalVictimMessage
                                    + "\n\n"
                                    + additionalItemsMessage;

                } else {

                    finalVictimMessage =
                            additionalItemsMessage;
                }
            }
        }


        // =====================================================
        // SAVE FINAL VICTIM MESSAGE
        // =====================================================

        reliefRequirement.setVictimMessage(
                finalVictimMessage
        );


        // =====================================================
        // SAVE STATUS
        // =====================================================

        if (sendToDistribution) {

            /*
             * IMPORTANT:
             *
             * The requirement is approved by the Relief
             * Coordinator before it is sent to Distribution
             * Management.
             *
             * Therefore the status is APPROVED.
             *
             * Distribution Management can then display
             * only APPROVED requirements.
             *
             * The package is NOT yet prepared at this stage.
             */

            reliefRequirement.setStatus(
                    "APPROVED"
            );

        } else if (status != null
                && !status.trim().isEmpty()) {

            /*
             * RC can save the requirement with its selected
             * status.
             */

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
                    "Relief requirement approved and sent to Distribution Management successfully."
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

        return "redirect:/relief-coordinator/dashboard";
    }
}