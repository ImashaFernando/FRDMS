package com.frdms.frdms.common.controller;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.repository.ReliefRequirementRepository;
import com.frdms.frdms.victim_management.service.NeedsAssessmentService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/relief-coordinator")
public class ReliefCoordinatorController {

    private final NeedsAssessmentService needsAssessmentService;

    private final ReliefRequirementRepository reliefRequirementRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReliefCoordinatorController(
            NeedsAssessmentService needsAssessmentService,
            ReliefRequirementRepository reliefRequirementRepository) {

        this.needsAssessmentService =
                needsAssessmentService;

        this.reliefRequirementRepository =
                reliefRequirementRepository;
    }


    // =========================================================
    // RELIEF COORDINATOR LOGIN PAGE
    // =========================================================

    @GetMapping("/login")
    public String showLoginPage() {

        return "login";
    }


    // =========================================================
    // RELIEF COORDINATOR DASHBOARD
    // =========================================================

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {


        // =====================================================
        // GET ALL NEEDS ASSESSMENTS
        // =====================================================

        List<NeedsAssessment> assessments =
                needsAssessmentService
                        .getAllAssessments();


        // =====================================================
        // COUNT SUBMITTED ASSESSMENTS
        // =====================================================

        /*
         * Submitted Assessments means all assessments that
         * have been submitted by victims.
         *
         * Therefore the following statuses are included:
         *
         * SUBMITTED
         * UPDATED
         * ASSESSED
         *
         * Example:
         *
         * Assessment 1 -> SUBMITTED
         * Assessment 2 -> UPDATED
         *
         * Submitted Assessments = 2
         */

        long submittedCount =
                assessments.stream()
                        .filter(a -> {

                            String status = a.getStatus();

                            if (status == null) {
                                return false;
                            }

                            return "SUBMITTED".equalsIgnoreCase(status)
                                    || "UPDATED".equalsIgnoreCase(status)
                                    || "ASSESSED".equalsIgnoreCase(status);
                        })
                        .count();


        // =====================================================
        // COUNT UPDATED ASSESSMENTS
        // =====================================================

        /*
         * Updated Assessments shows assessments whose
         * CURRENT status is UPDATED.
         */

        long updatedCount =
                assessments.stream()
                        .filter(a ->
                                a.getStatus() != null
                                        &&
                                        "UPDATED".equalsIgnoreCase(
                                                a.getStatus()
                                        )
                        )
                        .count();


        // =====================================================
        // COUNT ASSESSED ASSESSMENTS
        // =====================================================

        /*
         * An assessment is considered ASSESSED once the
         * Relief Coordinator has created a Relief Requirement
         * for that Needs Assessment.
         *
         * This means:
         *
         * Victim submits Needs Assessment
         *              ↓
         * RC opens the assessment
         *              ↓
         * RC creates/saves Relief Requirement
         *              ↓
         * Assessment appears as ASSESSED
         *
         * We also keep support for an existing NeedsAssessment
         * status of "ASSESSED".
         */

        long assessedCount = 0;


        for (NeedsAssessment assessment : assessments) {

            boolean isAssessedByStatus =
                    "ASSESSED".equalsIgnoreCase(
                            assessment.getStatus()
                    );


            boolean hasReliefRequirement =
                    reliefRequirementRepository
                            .findByNeedsAssessment(
                                    assessment
                            )
                            .isPresent();


            if (
                    isAssessedByStatus
                            ||
                            hasReliefRequirement
            ) {

                assessedCount++;
            }
        }


        // =====================================================
        // GET RELIEF STATUS FOR EACH ASSESSMENT
        // =====================================================

        /*
         * This map connects:
         *
         * Needs Assessment ID
         *
         * with
         *
         * Relief Requirement Status
         *
         * Example:
         *
         * Assessment ID 1 -> APPROVED
         * Assessment ID 2 -> PENDING
         * Assessment ID 3 -> READY FOR DISTRIBUTION
         * Assessment ID 4 -> COMPLETED
         */

        Map<Long, String> reliefStatuses =
                new HashMap<>();


        // =====================================================
        // PROCESS EACH ASSESSMENT
        // =====================================================

        for (NeedsAssessment assessment : assessments) {


            Optional<ReliefRequirement> requirement =
                    reliefRequirementRepository
                            .findByNeedsAssessment(
                                    assessment
                            );


            // =================================================
            // RELIEF REQUIREMENT EXISTS
            // =================================================

            if (requirement.isPresent()) {


                String status =
                        requirement
                                .get()
                                .getStatus();


                // ---------------------------------------------
                // STATUS EXISTS
                // ---------------------------------------------

                if (
                        status != null
                                &&
                                !status.trim().isEmpty()
                ) {


                    /*
                     * Convert the status to uppercase so that
                     * Thymeleaf receives a consistent value.
                     *
                     * Pending
                     * pending
                     * PENDING
                     *
                     * all become:
                     *
                     * PENDING
                     */

                    reliefStatuses.put(
                            assessment.getId(),
                            status
                                    .trim()
                                    .toUpperCase()
                    );


                }

                // ---------------------------------------------
                // STATUS IS EMPTY
                // ---------------------------------------------

                else {


                    reliefStatuses.put(
                            assessment.getId(),
                            "PENDING"
                    );
                }


            }

            // =================================================
            // NO RELIEF REQUIREMENT
            // =================================================

            else {


                reliefStatuses.put(
                        assessment.getId(),
                        "NOT ASSESSED"
                );
            }
        }


        // =====================================================
        // SEND ASSESSMENTS TO THYMELEAF
        // =====================================================

        model.addAttribute(
                "assessments",
                assessments
        );


        // =====================================================
        // SEND SUBMITTED COUNT
        // =====================================================

        model.addAttribute(
                "submittedCount",
                submittedCount
        );


        // =====================================================
        // SEND UPDATED COUNT
        // =====================================================

        model.addAttribute(
                "updatedCount",
                updatedCount
        );


        // =====================================================
        // SEND ASSESSED COUNT
        // =====================================================

        model.addAttribute(
                "assessedCount",
                assessedCount
        );


        // =====================================================
        // SEND RELIEF STATUS MAP
        // =====================================================

        model.addAttribute(
                "reliefStatuses",
                reliefStatuses
        );


        // =====================================================
        // OPEN DASHBOARD
        // =====================================================

        return "relief-coordinator/dashboard";
    }


    // =========================================================
    // VIEW ALL NEEDS ASSESSMENTS
    // =========================================================

    @GetMapping("/needs-assessments")
    public String viewNeedsAssessments(Model model) {


        model.addAttribute(
                "assessments",
                needsAssessmentService
                        .getAllAssessments()
        );


        return "relief-coordinator/needs-assessments";
    }


    // =========================================================
    // VIEW ONE NEEDS ASSESSMENT
    // =========================================================

    @GetMapping("/needs-assessments/{id}")
    public String viewNeedsAssessment(
            @PathVariable Long id,
            Model model) {


        NeedsAssessment assessment =
                needsAssessmentService
                        .getAssessmentById(id);


        // =====================================================
        // SEND ASSESSMENT
        // =====================================================

        model.addAttribute(
                "assessment",
                assessment
        );


        // =====================================================
        // SEND VICTIM
        // =====================================================

        model.addAttribute(
                "victim",
                assessment.getVictim()
        );


        return "relief-coordinator/needs-assessment-details";
    }


    // =========================================================
    // GIVE FIRST ASSESSMENT
    // =========================================================

    @PostMapping("/needs-assessments/{id}/review")
    public String reviewNeedsAssessment(
            @PathVariable Long id,
            @RequestParam String priority,
            @RequestParam String assessmentRemarks,
            @RequestParam String assessedBy) {


        /*
         * Existing assessment review functionality is
         * preserved.
         */

        needsAssessmentService.updateAssessment(
                id,
                priority,
                assessmentRemarks,
                assessedBy,
                "ASSESSED"
        );


        return "redirect:/relief-coordinator/needs-assessments/"
                + id;
    }


    // =========================================================
    // SHOW UPDATE ASSESSMENT PAGE
    // =========================================================

    @GetMapping("/needs-assessments/{id}/edit")
    public String showEditAssessment(
            @PathVariable Long id,
            Model model) {


        NeedsAssessment assessment =
                needsAssessmentService
                        .getAssessmentById(id);


        // =====================================================
        // SEND ASSESSMENT
        // =====================================================

        model.addAttribute(
                "assessment",
                assessment
        );


        // =====================================================
        // SEND VICTIM
        // =====================================================

        model.addAttribute(
                "victim",
                assessment.getVictim()
        );


        return "relief-coordinator/edit-needs-assessment";
    }


    // =========================================================
    // UPDATE EXISTING RC ASSESSMENT
    // =========================================================

    @PostMapping("/needs-assessments/{id}/update")
    public String updateAssessment(
            @PathVariable Long id,
            @RequestParam String priority,
            @RequestParam String assessmentRemarks,
            @RequestParam String assessedBy) {


        /*
         * Existing update functionality is preserved.
         */

        needsAssessmentService.updateAssessment(
                id,
                priority,
                assessmentRemarks,
                assessedBy,
                "ASSESSED"
        );


        return "redirect:/relief-coordinator/needs-assessments/"
                + id;
    }


}