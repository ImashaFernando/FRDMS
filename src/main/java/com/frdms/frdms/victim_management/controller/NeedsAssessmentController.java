package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.NeedsAssessmentService;
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

    public NeedsAssessmentController(
            NeedsAssessmentService needsAssessmentService,
            VictimService victimService) {

        this.needsAssessmentService = needsAssessmentService;
        this.victimService = victimService;
    }


    // =========================================================
    // VICTIM SIDE
    // =========================================================

    /*
     * Show the Needs Assessment form.
     *
     * If the victim already has an assessment,
     * the same existing assessment is loaded.
     *
     * If the victim does not have an assessment,
     * a new blank assessment is created.
     */

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


        // -----------------------------------------------------
        // IF NO ASSESSMENT EXISTS
        // CREATE A NEW BLANK OBJECT
        // -----------------------------------------------------

        if (assessment == null) {

            assessment = new NeedsAssessment();

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


        // -----------------------------------------------------
        // VALIDATION ERRORS
        // -----------------------------------------------------

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "victim",
                    victim
            );

            return "victim/needs-assessment";
        }


        // =====================================================
        // FIND EXISTING ASSESSMENT FOR THIS VICTIM
        // =====================================================

        NeedsAssessment existingAssessment =
                needsAssessmentService
                        .getLatestAssessmentByVictim(victim);


        // =====================================================
        // EXISTING ASSESSMENT FOUND
        // UPDATE THE SAME RECORD
        // =====================================================

        if (existingAssessment != null) {

            // -------------------------------------------------
            // UPDATE CURRENT SITUATION
            // -------------------------------------------------

            existingAssessment.setCurrentSituation(
                    submittedAssessment.getCurrentSituation()
            );


            // -------------------------------------------------
            // UPDATE FOOD REQUIREMENT
            // -------------------------------------------------

            existingAssessment.setFoodRequired(
                    submittedAssessment.getFoodRequired()
            );


            // -------------------------------------------------
            // UPDATE WATER REQUIREMENT
            // -------------------------------------------------

            existingAssessment.setWaterRequired(
                    submittedAssessment.getWaterRequired()
            );


            // -------------------------------------------------
            // UPDATE MEDICAL ASSISTANCE
            // -------------------------------------------------

            existingAssessment.setMedicalAssistanceRequired(
                    submittedAssessment
                            .getMedicalAssistanceRequired()
            );


            // -------------------------------------------------
            // UPDATE CLOTHING REQUIREMENT
            // -------------------------------------------------

            existingAssessment.setClothingRequired(
                    submittedAssessment.getClothingRequired()
            );


            // -------------------------------------------------
            // UPDATE HYGIENE ITEMS
            // -------------------------------------------------

            existingAssessment.setHygieneItemsRequired(
                    submittedAssessment
                            .getHygieneItemsRequired()
            );


            // -------------------------------------------------
            // UPDATE OTHER NEEDS
            // -------------------------------------------------

            existingAssessment.setOtherNeeds(
                    submittedAssessment.getOtherNeeds()
            );


            // -------------------------------------------------
            // UPDATE ADDITIONAL INFORMATION
            // -------------------------------------------------

            existingAssessment.setAdditionalInformation(
                    submittedAssessment
                            .getAdditionalInformation()
            );


            // -------------------------------------------------
            // KEEP THE SAME VICTIM
            // -------------------------------------------------

            existingAssessment.setVictim(victim);


            // -------------------------------------------------
            // MARK AS UPDATED
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
            // SAVE SAME DATABASE RECORD
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
            // CONNECT ASSESSMENT TO VICTIM
            // -------------------------------------------------

            submittedAssessment.setVictim(
                    victim
            );


            // -------------------------------------------------
            // SET INITIAL STATUS
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
            // CREATE NEW DATABASE RECORD
            // -------------------------------------------------

            needsAssessmentService.saveAssessment(
                    submittedAssessment
            );
        }


        // -----------------------------------------------------
        // REDIRECT TO STATUS PAGE
        // -----------------------------------------------------

        return "redirect:/needs-assessment/status";
    }


    // =========================================================
    // VICTIM STATUS
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
        // GET LOGGED-IN VICTIM
        // -----------------------------------------------------

        Victim victim =
                victimService.getVictimById(victimId);

        if (victim == null) {
            session.invalidate();
            return "redirect:/login";
        }


        // -----------------------------------------------------
        // GET LATEST ASSESSMENT
        // -----------------------------------------------------

        NeedsAssessment assessment =
                needsAssessmentService
                        .getLatestAssessmentByVictim(victim);


        if (assessment != null) {

            model.addAttribute(
                    "needsAssessment",
                    assessment
            );
        }


        // -----------------------------------------------------
        // SEND VICTIM TO PAGE
        // -----------------------------------------------------

        model.addAttribute(
                "victim",
                victim
        );


        return "victim/needs-assessment-status";
    }
}