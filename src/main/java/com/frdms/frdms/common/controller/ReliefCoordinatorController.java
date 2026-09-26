package com.frdms.frdms.common.controller;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.service.NeedsAssessmentService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/relief-coordinator")
public class ReliefCoordinatorController {

    private final NeedsAssessmentService needsAssessmentService;

    public ReliefCoordinatorController(
            NeedsAssessmentService needsAssessmentService) {

        this.needsAssessmentService = needsAssessmentService;
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

        var assessments =
                needsAssessmentService.getAllAssessments();

        long submittedCount =
                assessments.stream()
                        .filter(a ->
                                "SUBMITTED".equals(a.getStatus()))
                        .count();

        long updatedCount =
                assessments.stream()
                        .filter(a ->
                                "UPDATED".equals(a.getStatus()))
                        .count();

        long assessedCount =
                assessments.stream()
                        .filter(a ->
                                "ASSESSED".equals(a.getStatus()))
                        .count();

        model.addAttribute(
                "assessments",
                assessments
        );

        model.addAttribute(
                "submittedCount",
                submittedCount
        );

        model.addAttribute(
                "updatedCount",
                updatedCount
        );

        model.addAttribute(
                "assessedCount",
                assessedCount
        );

        return "relief-coordinator/dashboard";
    }


    // =========================================================
    // VIEW ALL NEEDS ASSESSMENTS
    // =========================================================

    @GetMapping("/needs-assessments")
    public String viewNeedsAssessments(Model model) {

        model.addAttribute(
                "assessments",
                needsAssessmentService.getAllAssessments()
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
                needsAssessmentService.getAssessmentById(id);

        model.addAttribute(
                "assessment",
                assessment
        );

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

        needsAssessmentService.updateAssessment(
                id,
                priority,
                assessmentRemarks,
                assessedBy,
                "ASSESSED"
        );

        return "redirect:/relief-coordinator/needs-assessments/" + id;
    }


    // =========================================================
    // SHOW UPDATE ASSESSMENT PAGE
    // =========================================================

    @GetMapping("/needs-assessments/{id}/edit")
    public String showEditAssessment(
            @PathVariable Long id,
            Model model) {

        NeedsAssessment assessment =
                needsAssessmentService.getAssessmentById(id);

        model.addAttribute(
                "assessment",
                assessment
        );

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
         * IMPORTANT:
         *
         * The existing Needs Assessment ID is used.
         *
         * Therefore this operation updates the existing
         * database record instead of creating a new record.
         */

        needsAssessmentService.updateAssessment(
                id,
                priority,
                assessmentRemarks,
                assessedBy,
                "ASSESSED"
        );

        return "redirect:/relief-coordinator/needs-assessments/" + id;
    }

}