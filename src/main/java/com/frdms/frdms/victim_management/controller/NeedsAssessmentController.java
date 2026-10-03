package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.entity.Victim;

import com.frdms.frdms.victim_management.service.NeedsAssessmentService;
import com.frdms.frdms.victim_management.service.ReliefRequirementService;
import com.frdms.frdms.victim_management.service.VictimService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/needs-assessment")
public class NeedsAssessmentController {


    private final NeedsAssessmentService needsAssessmentService;

    private final VictimService victimService;

    private final ReliefRequirementService reliefRequirementService;


    public NeedsAssessmentController(

            NeedsAssessmentService needsAssessmentService,

            VictimService victimService,

            ReliefRequirementService reliefRequirementService) {

        this.needsAssessmentService =
                needsAssessmentService;

        this.victimService =
                victimService;

        this.reliefRequirementService =
                reliefRequirementService;
    }



    // =========================================================
    // SHOW NEEDS ASSESSMENT FORM
    // =========================================================

    @GetMapping("/form")
    public String showNeedsAssessmentForm(

            HttpSession session,

            Model model) {


        // -----------------------------------------------------
        // CHECK VICTIM LOGIN
        // -----------------------------------------------------

        Long victimId =
                (Long) session.getAttribute("victimId");


        if (victimId == null) {

            return "redirect:/login";
        }



        // -----------------------------------------------------
        // GET LOGGED-IN VICTIM
        // -----------------------------------------------------

        Victim victim =
                victimService.getVictimById(victimId);


        if (victim == null) {

            session.invalidate();

            return "redirect:/login";
        }



        // -----------------------------------------------------
        // FIND EXISTING ASSESSMENT
        // -----------------------------------------------------

        NeedsAssessment assessment =

                needsAssessmentService
                        .getLatestAssessmentByVictim(victim);



        // =====================================================
        // CHECK WHETHER RELIEF REQUIREMENT WAS ALREADY SENT
        // =====================================================

        if (assessment != null) {


            var existingReliefRequirement =

                    reliefRequirementService
                            .getByNeedsAssessment(
                                    assessment
                            );


            /*
             * If the Relief Coordinator has already
             * created/sent a relief requirement for
             * this assessment, the victim must not
             * update the assessment anymore.
             */

            if (existingReliefRequirement.isPresent()) {

                return "redirect:/needs-assessment/status";
            }
        }



        // -----------------------------------------------------
        // IF NO ASSESSMENT EXISTS
        // CREATE NEW BLANK OBJECT
        // -----------------------------------------------------

        if (assessment == null) {

            assessment =
                    new NeedsAssessment();

            assessment.setVictim(victim);
        }



        // -----------------------------------------------------
        // SEND DATA TO THYMELEAF
        // -----------------------------------------------------

        model.addAttribute(
                "victim",
                victim
        );


        model.addAttribute(
                "needsAssessment",
                assessment
        );



        return "victim/needs-assessment";
    }



    // =========================================================
    // SUBMIT / UPDATE NEEDS ASSESSMENT
    // =========================================================

    @PostMapping("/submit")
    public String submitNeedsAssessment(

            @Valid
            @ModelAttribute("needsAssessment")
            NeedsAssessment submittedAssessment,

            BindingResult bindingResult,

            HttpSession session,

            Model model) {


        // -----------------------------------------------------
        // CHECK VICTIM LOGIN
        // -----------------------------------------------------

        Long victimId =

                (Long) session.getAttribute("victimId");


        if (victimId == null) {

            return "redirect:/login";
        }



        // -----------------------------------------------------
        // GET LOGGED-IN VICTIM
        // -----------------------------------------------------

        Victim victim =

                victimService.getVictimById(victimId);


        if (victim == null) {

            session.invalidate();

            return "redirect:/login";
        }



        // =====================================================
        // FIND EXISTING ASSESSMENT
        // =====================================================

        NeedsAssessment existingAssessment =

                needsAssessmentService
                        .getLatestAssessmentByVictim(victim);



        // =====================================================
        // IMPORTANT LOCK
        // =====================================================

        /*
         * If an existing assessment already has a
         * Relief Requirement created by the Relief
         * Coordinator, the victim is NOT allowed
         * to update it.
         *
         * This is the backend protection.
         */

        if (existingAssessment != null) {


            var existingReliefRequirement =

                    reliefRequirementService
                            .getByNeedsAssessment(
                                    existingAssessment
                            );


            if (existingReliefRequirement.isPresent()) {

                return "redirect:/needs-assessment/status";
            }
        }



        // -----------------------------------------------------
        // VALIDATION ERRORS
        // -----------------------------------------------------

        if (bindingResult.hasErrors()) {


            model.addAttribute(
                    "victim",
                    victim
            );


            model.addAttribute(
                    "needsAssessment",
                    submittedAssessment
            );


            return "victim/needs-assessment";
        }



        // =====================================================
        // EXISTING ASSESSMENT FOUND
        // UPDATE SAME RECORD
        // =====================================================

        if (existingAssessment != null) {


            // -------------------------------------------------
            // CURRENT SITUATION
            // -------------------------------------------------

            existingAssessment.setCurrentSituation(

                    submittedAssessment
                            .getCurrentSituation()

            );



            // -------------------------------------------------
            // FOOD
            // -------------------------------------------------

            existingAssessment.setFoodRequired(

                    submittedAssessment
                            .getFoodRequired()

            );



            // -------------------------------------------------
            // WATER
            // -------------------------------------------------

            existingAssessment.setWaterRequired(

                    submittedAssessment
                            .getWaterRequired()

            );



            // -------------------------------------------------
            // MEDICAL ASSISTANCE
            // -------------------------------------------------

            existingAssessment
                    .setMedicalAssistanceRequired(

                            submittedAssessment
                                    .getMedicalAssistanceRequired()

                    );



            // -------------------------------------------------
            // CLOTHING
            // -------------------------------------------------

            existingAssessment.setClothingRequired(

                    submittedAssessment
                            .getClothingRequired()

            );



            // -------------------------------------------------
            // HYGIENE ITEMS
            // -------------------------------------------------

            existingAssessment
                    .setHygieneItemsRequired(

                            submittedAssessment
                                    .getHygieneItemsRequired()

                    );



            // -------------------------------------------------
            // OTHER NEEDS
            // -------------------------------------------------

            existingAssessment.setOtherNeeds(

                    submittedAssessment
                            .getOtherNeeds()

            );



            // -------------------------------------------------
            // ADDITIONAL INFORMATION
            // -------------------------------------------------

            existingAssessment
                    .setAdditionalInformation(

                            submittedAssessment
                                    .getAdditionalInformation()

                    );



            // -------------------------------------------------
            // KEEP SAME VICTIM
            // -------------------------------------------------

            existingAssessment.setVictim(victim);



            // -------------------------------------------------
            // MARK UPDATED
            // -------------------------------------------------

            existingAssessment.setStatus(
                    "UPDATED"
            );



            // -------------------------------------------------
            // CLEAR OLD COORDINATOR ASSESSMENT
            // -------------------------------------------------

            existingAssessment.setPriority(null);

            existingAssessment.setAssessmentRemarks(null);

            existingAssessment.setAssessedBy(null);

            existingAssessment.setAssessmentDate(null);



            // -------------------------------------------------
            // SAVE SAME RECORD
            // -------------------------------------------------

            needsAssessmentService.saveAssessment(

                    existingAssessment

            );

        }



        // =====================================================
        // NO EXISTING ASSESSMENT
        // CREATE FIRST ASSESSMENT
        // =====================================================

        else {


            // -------------------------------------------------
            // CONNECT TO VICTIM
            // -------------------------------------------------

            submittedAssessment.setVictim(
                    victim
            );



            // -------------------------------------------------
            // INITIAL STATUS
            // -------------------------------------------------

            submittedAssessment.setStatus(
                    "SUBMITTED"
            );



            // -------------------------------------------------
            // CLEAR COORDINATOR FIELDS
            // -------------------------------------------------

            submittedAssessment.setPriority(null);

            submittedAssessment.setAssessmentRemarks(null);

            submittedAssessment.setAssessedBy(null);

            submittedAssessment.setAssessmentDate(null);



            // -------------------------------------------------
            // SAVE
            // -------------------------------------------------

            needsAssessmentService.saveAssessment(

                    submittedAssessment

            );
        }



        // -----------------------------------------------------
        // GO TO STATUS PAGE
        // -----------------------------------------------------

        return "redirect:/needs-assessment/status";
    }



    // =========================================================
    // VIEW NEEDS ASSESSMENT STATUS
    // =========================================================

    @GetMapping("/status")
    public String viewAssessmentStatus(

            HttpSession session,

            Model model) {


        // -----------------------------------------------------
        // CHECK VICTIM LOGIN
        // -----------------------------------------------------

        Long victimId =

                (Long) session.getAttribute("victimId");


        if (victimId == null) {

            return "redirect:/login";
        }



        // -----------------------------------------------------
        // GET VICTIM
        // -----------------------------------------------------

        Victim victim =

                victimService.getVictimById(victimId);


        if (victim == null) {

            session.invalidate();

            return "redirect:/login";
        }



        // -----------------------------------------------------
        // GET LATEST NEEDS ASSESSMENT
        // -----------------------------------------------------

        NeedsAssessment assessment =

                needsAssessmentService
                        .getLatestAssessmentByVictim(victim);



        // -----------------------------------------------------
        // ADD ASSESSMENT
        // -----------------------------------------------------

        if (assessment != null) {

            model.addAttribute(
                    "needsAssessment",
                    assessment
            );


            // =================================================
            // GET RELIEF REQUIREMENT FOR THIS ASSESSMENT
            // =================================================

            var reliefRequirement =

                    reliefRequirementService
                            .getByNeedsAssessment(
                                    assessment
                            );


            if (reliefRequirement.isPresent()) {

                model.addAttribute(

                        "latestReliefRequirement",

                        reliefRequirement.get()

                );

            }

        }



        // -----------------------------------------------------
        // ADD VICTIM
        // -----------------------------------------------------

        model.addAttribute(
                "victim",
                victim
        );



        return "victim/needs-assessment-status";
    }

}