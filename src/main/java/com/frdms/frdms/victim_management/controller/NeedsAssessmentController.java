package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.GNReportService;
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

    private final GNReportService gnReportService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public NeedsAssessmentController(
            NeedsAssessmentService needsAssessmentService,
            VictimService victimService,
            ReliefRequirementService reliefRequirementService,
            GNReportService gnReportService) {

        this.needsAssessmentService =
                needsAssessmentService;

        this.victimService =
                victimService;

        this.reliefRequirementService =
                reliefRequirementService;

        this.gnReportService =
                gnReportService;
    }


    // =========================================================
    // SHOW NEEDS ASSESSMENT FORM
    // =========================================================

    @GetMapping("/form")
    public String showNeedsAssessmentForm(
            HttpSession session,
            Model model) {


        // =====================================================
        // CHECK VICTIM LOGIN
        // =====================================================

        Long victimId =
                (Long) session.getAttribute(
                        "victimId"
                );


        if (victimId == null) {

            return "redirect:/login";
        }


        // =====================================================
        // GET VICTIM
        // =====================================================

        Victim victim;

        try {

            victim =
                    victimService.getVictimById(
                            victimId
                    );

        } catch (Exception e) {

            session.invalidate();

            return "redirect:/login";
        }


        if (victim == null) {

            session.invalidate();

            return "redirect:/login";
        }


        // =====================================================
        // CHECK GN REPORT APPROVAL
        // =====================================================

        /*
         * The victim cannot access the Needs Assessment
         * until the latest GN Report has been APPROVED.
         */

        boolean gnReportApproved =
                gnReportService
                        .isLatestGNReportApproved(
                                victim
                        );


        if (!gnReportApproved) {

            /*
             * Send the victim back to the dashboard.
             *
             * The dashboard can then show that the
             * GN Report is still waiting for approval.
             */

            return "redirect:/victim/dashboard?gnApprovalRequired=true";
        }


        // =====================================================
        // GET EXISTING NEEDS ASSESSMENT
        // =====================================================

        NeedsAssessment assessment =
                needsAssessmentService
                        .getLatestAssessmentByVictim(
                                victim
                        );


        // =====================================================
        // CHECK EXISTING RELIEF REQUIREMENT
        // =====================================================

        if (assessment != null) {

            var existingReliefRequirement =
                    reliefRequirementService
                            .getByNeedsAssessment(
                                    assessment
                            );


            if (existingReliefRequirement.isPresent()) {

                return "redirect:/needs-assessment/status";
            }
        }


        // =====================================================
        // CREATE NEW ASSESSMENT
        // =====================================================

        if (assessment == null) {

            assessment =
                    new NeedsAssessment();

            assessment.setVictim(
                    victim
            );


            /*
             * Food is no longer collected from the
             * Victim Needs Assessment form.
             */
            assessment.setFoodRequired(
                    false
            );


            /*
             * New Beddings option.
             */
            assessment.setBeddingRequired(
                    false
            );


            assessment.setWaterRequired(
                    false
            );


            assessment.setMedicalAssistanceRequired(
                    false
            );


            /*
             * Clothing is no longer collected.
             */
            assessment.setClothingRequired(
                    false
            );


            assessment.setHygieneItemsRequired(
                    false
            );


            assessment.setStatus(
                    "SUBMITTED"
            );
        }


        // =====================================================
        // SEND DATA TO VIEW
        // =====================================================

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
    // SUBMIT NEEDS ASSESSMENT
    // =========================================================

    @PostMapping("/submit")
    public String submitNeedsAssessment(

            @Valid
            @ModelAttribute("needsAssessment")
            NeedsAssessment submittedAssessment,

            BindingResult bindingResult,

            HttpSession session,

            Model model) {


        // =====================================================
        // CHECK VICTIM LOGIN
        // =====================================================

        Long victimId =
                (Long) session.getAttribute(
                        "victimId"
                );


        if (victimId == null) {

            return "redirect:/login";
        }


        // =====================================================
        // GET VICTIM
        // =====================================================

        Victim victim;

        try {

            victim =
                    victimService.getVictimById(
                            victimId
                    );

        } catch (Exception e) {

            session.invalidate();

            return "redirect:/login";
        }


        if (victim == null) {

            session.invalidate();

            return "redirect:/login";
        }


        // =====================================================
        // CHECK GN REPORT APPROVAL AGAIN
        // =====================================================

        /*
         * IMPORTANT:
         *
         * We check this again during POST.
         *
         * This prevents a victim from bypassing the rule
         * by manually submitting a POST request.
         */

        boolean gnReportApproved =
                gnReportService
                        .isLatestGNReportApproved(
                                victim
                        );


        if (!gnReportApproved) {

            return "redirect:/victim/dashboard?gnApprovalRequired=true";
        }


        // =====================================================
        // NORMALIZE CHECKBOX VALUES
        // =====================================================

        /*
         * Food is no longer part of the Victim form.
         *
         * Keep the old field false so existing database
         * compatibility is maintained.
         */

        submittedAssessment.setFoodRequired(
                false
        );


        /*
         * Beddings
         *
         * If the checkbox is not selected,
         * Thymeleaf may submit null.
         *
         * Convert null to false.
         */

        if (submittedAssessment
                .getBeddingRequired() == null) {

            submittedAssessment
                    .setBeddingRequired(
                            false
                    );
        }


        if (submittedAssessment
                .getWaterRequired() == null) {

            submittedAssessment.setWaterRequired(
                    false
            );
        }


        if (submittedAssessment
                .getMedicalAssistanceRequired() == null) {

            submittedAssessment
                    .setMedicalAssistanceRequired(
                            false
                    );
        }


        /*
         * Clothing is no longer part of the
         * Victim Needs Assessment form.
         */

        submittedAssessment.setClothingRequired(
                false
        );


        if (submittedAssessment
                .getHygieneItemsRequired() == null) {

            submittedAssessment
                    .setHygieneItemsRequired(
                            false
                    );
        }


        // =====================================================
        // VALIDATION ERRORS
        // =====================================================

        if (bindingResult.hasErrors()) {

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "NEEDS ASSESSMENT VALIDATION ERROR"
            );

            System.out.println(
                    "=========================================="
            );


            bindingResult
                    .getAllErrors()
                    .forEach(error ->
                            System.out.println(
                                    "ERROR: "
                                            + error
                                            .getDefaultMessage()
                            )
                    );


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
        // GET EXISTING ASSESSMENT
        // =====================================================

        NeedsAssessment existingAssessment =
                needsAssessmentService
                        .getLatestAssessmentByVictim(
                                victim
                        );


        // =====================================================
        // CHECK EXISTING RELIEF REQUIREMENT
        // =====================================================

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


        // =====================================================
        // UPDATE EXISTING ASSESSMENT
        // =====================================================

        if (existingAssessment != null) {

            existingAssessment.setCurrentSituation(
                    submittedAssessment
                            .getCurrentSituation()
            );


            /*
             * Food is no longer collected.
             */
            existingAssessment.setFoodRequired(
                    false
            );


            /*
             * Save Beddings.
             */
            existingAssessment.setBeddingRequired(
                    submittedAssessment
                            .getBeddingRequired()
            );


            existingAssessment.setWaterRequired(
                    submittedAssessment
                            .getWaterRequired()
            );


            existingAssessment
                    .setMedicalAssistanceRequired(
                            submittedAssessment
                                    .getMedicalAssistanceRequired()
                    );


            /*
             * Clothing is no longer collected.
             */
            existingAssessment
                    .setClothingRequired(
                            false
                    );


            existingAssessment
                    .setHygieneItemsRequired(
                            submittedAssessment
                                    .getHygieneItemsRequired()
                    );


            existingAssessment.setOtherNeeds(
                    submittedAssessment
                            .getOtherNeeds()
            );


            existingAssessment
                    .setAdditionalInformation(
                            submittedAssessment
                                    .getAdditionalInformation()
                    );


            existingAssessment.setVictim(
                    victim
            );


            existingAssessment.setPriority(
                    null
            );


            existingAssessment
                    .setAssessmentRemarks(
                            null
                    );


            existingAssessment.setAssessedBy(
                    null
            );


            existingAssessment.setAssessmentDate(
                    null
            );


            /*
             * Keep the assessment in a state that
             * indicates it has been submitted again.
             */

            existingAssessment.setStatus(
                    "UPDATED"
            );


            needsAssessmentService
                    .saveAssessment(
                            existingAssessment
                    );


        } else {


            // =================================================
            // CREATE NEW ASSESSMENT
            // =================================================

            submittedAssessment.setVictim(
                    victim
            );


            submittedAssessment.setStatus(
                    "SUBMITTED"
            );


            /*
             * Food is no longer collected.
             */
            submittedAssessment
                    .setFoodRequired(
                            false
                    );


            /*
             * Beddings is now the replacement
             * assistance option.
             */
            if (submittedAssessment
                    .getBeddingRequired() == null) {

                submittedAssessment
                        .setBeddingRequired(
                                false
                        );
            }


            /*
             * Clothing is no longer collected.
             */
            submittedAssessment
                    .setClothingRequired(
                            false
                    );


            submittedAssessment.setPriority(
                    null
            );


            submittedAssessment
                    .setAssessmentRemarks(
                            null
                    );


            submittedAssessment.setAssessedBy(
                    null
            );


            submittedAssessment.setAssessmentDate(
                    null
            );


            needsAssessmentService
                    .saveAssessment(
                            submittedAssessment
                    );
        }


        // =====================================================
        // SUCCESS
        // =====================================================

        return "redirect:/victim/dashboard?assessmentSuccess=true";
    }


    // =========================================================
    // VIEW NEEDS ASSESSMENT STATUS
    // =========================================================

    @GetMapping("/status")
    public String viewAssessmentStatus(
            HttpSession session,
            Model model) {


        // =====================================================
        // CHECK LOGIN
        // =====================================================

        Long victimId =
                (Long) session.getAttribute(
                        "victimId"
                );


        if (victimId == null) {

            return "redirect:/login";
        }


        // =====================================================
        // GET VICTIM
        // =====================================================

        Victim victim;

        try {

            victim =
                    victimService.getVictimById(
                            victimId
                    );

        } catch (Exception e) {

            session.invalidate();

            return "redirect:/login";
        }


        if (victim == null) {

            session.invalidate();

            return "redirect:/login";
        }


        // =====================================================
        // GET NEEDS ASSESSMENT
        // =====================================================

        NeedsAssessment assessment =
                needsAssessmentService
                        .getLatestAssessmentByVictim(
                                victim
                        );


        if (assessment != null) {

            model.addAttribute(
                    "needsAssessment",
                    assessment
            );


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


        // =====================================================
        // SEND VICTIM
        // =====================================================

        model.addAttribute(
                "victim",
                victim
        );


        return "victim/needs-assessment-status";
    }
}