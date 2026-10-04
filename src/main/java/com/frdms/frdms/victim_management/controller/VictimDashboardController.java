package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.entity.GNReport;
import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.entity.Victim;

import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;
import com.frdms.frdms.victim_management.service.GNReportService;
import com.frdms.frdms.victim_management.service.NeedsAssessmentService;
import com.frdms.frdms.victim_management.service.ReliefRequirementService;
import com.frdms.frdms.victim_management.service.VictimService;

import com.frdms.frdms.shelter_management.entity.ShelterAllocation;
import com.frdms.frdms.shelter_management.service.ShelterAllocationService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class VictimDashboardController {

    private final VictimService victimService;

    private final GNReportService gnReportService;

    private final AffectedFamilyInformationService
            affectedFamilyInformationService;

    private final ShelterAllocationService
            shelterAllocationService;

    private final ReliefRequirementService
            reliefRequirementService;

    private final NeedsAssessmentService
            needsAssessmentService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimDashboardController(

            VictimService victimService,

            GNReportService gnReportService,

            AffectedFamilyInformationService
                    affectedFamilyInformationService,

            ShelterAllocationService
                    shelterAllocationService,

            ReliefRequirementService
                    reliefRequirementService,

            NeedsAssessmentService
                    needsAssessmentService) {

        this.victimService =
                victimService;

        this.gnReportService =
                gnReportService;

        this.affectedFamilyInformationService =
                affectedFamilyInformationService;

        this.shelterAllocationService =
                shelterAllocationService;

        this.reliefRequirementService =
                reliefRequirementService;

        this.needsAssessmentService =
                needsAssessmentService;
    }


    // =========================================================
    // VICTIM DASHBOARD
    // =========================================================

    @GetMapping("/victim/dashboard")
    public String showDashboard(
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
        // GET LOGGED-IN VICTIM
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


        // =====================================================
        // VICTIM NOT FOUND
        // =====================================================

        if (victim == null) {

            session.invalidate();

            return "redirect:/login";
        }


        // =====================================================
        // GET LATEST GN REPORT
        // =====================================================

        GNReport gnReport =
                gnReportService
                        .getLatestGNReportByVictim(
                                victim
                        );


        // =====================================================
        // GET FAMILY INFORMATION
        // =====================================================

        AffectedFamilyInformation familyInformation =
                affectedFamilyInformationService
                        .getByVictim(victim)
                        .orElse(null);


        // =====================================================
        // GET SHELTER ALLOCATION
        // =====================================================

        ShelterAllocation shelterAllocation =
                null;


        if (familyInformation != null
                && Boolean.TRUE.equals(
                familyInformation
                        .getShelterRequired())) {


            if (shelterAllocationService
                    .isFamilyAllocated(
                            familyInformation.getId()
                    )) {


                shelterAllocation =
                        shelterAllocationService
                                .getAllocationByFamilyId(
                                        familyInformation.getId()
                                );
            }
        }


        // =====================================================
        // GET LATEST NEEDS ASSESSMENT
        // =====================================================

        /*
         * This is important.
         *
         * The Victim Dashboard needs to know whether
         * the victim has already submitted a Needs Assessment.
         *
         * If an assessment exists but there is no Relief
         * Requirement yet, the dashboard will display:
         *
         * "Needs Assessment Submitted - PENDING"
         *
         * The victim must wait until the Relief Coordinator
         * reviews the assessment.
         */

        NeedsAssessment needsAssessment =
                needsAssessmentService
                        .getLatestAssessmentByVictim(victim);


        // =====================================================
        // GET RELIEF REQUIREMENTS
        // =====================================================

        /*
         * Get all Relief Requirements belonging to
         * the currently logged-in victim.
         */

        List<ReliefRequirement> reliefRequirements =
                reliefRequirementService
                        .getByVictim(victim);


        // =====================================================
        // GET LATEST RELIEF REQUIREMENT
        // =====================================================

        ReliefRequirement latestReliefRequirement =
                null;


        if (reliefRequirements != null
                && !reliefRequirements.isEmpty()) {

            latestReliefRequirement =
                    reliefRequirements.get(
                            reliefRequirements.size() - 1
                    );
        }


        // =====================================================
        // SEND VICTIM INFORMATION TO DASHBOARD
        // =====================================================

        model.addAttribute(
                "victim",
                victim
        );


        // =====================================================
        // SEND GN REPORT INFORMATION
        // =====================================================

        model.addAttribute(
                "gnReport",
                gnReport
        );


        // =====================================================
        // SEND FAMILY INFORMATION
        // =====================================================

        model.addAttribute(
                "familyInformation",
                familyInformation
        );


        // =====================================================
        // SEND SHELTER ALLOCATION
        // =====================================================

        model.addAttribute(
                "shelterAllocation",
                shelterAllocation
        );


        // =====================================================
        // SEND NEEDS ASSESSMENT
        // =====================================================

        /*
         * This allows dashboard.html to check:
         *
         * needsAssessment == null
         *
         * OR
         *
         * needsAssessment != null
         */

        model.addAttribute(
                "needsAssessment",
                needsAssessment
        );


        // =====================================================
        // SEND ALL RELIEF REQUIREMENTS
        // =====================================================

        model.addAttribute(
                "reliefRequirements",
                reliefRequirements
        );


        // =====================================================
        // SEND LATEST RELIEF REQUIREMENT
        // =====================================================

        model.addAttribute(
                "latestReliefRequirement",
                latestReliefRequirement
        );


        // =====================================================
        // OPEN VICTIM DASHBOARD
        // =====================================================

        return "victim/dashboard";
    }


    // =========================================================
    // UPDATE VICTIM PROFILE
    // =========================================================

    @PostMapping("/victim/dashboard/update-profile")
    public String updateProfile(

            @RequestParam String fullName,

            @RequestParam String phoneNumber,

            @RequestParam String address,

            HttpSession session,

            RedirectAttributes redirectAttributes) {


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
        // GET LOGGED-IN VICTIM
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


        // =====================================================
        // CHECK VICTIM EXISTS
        // =====================================================

        if (victim == null) {

            session.invalidate();

            return "redirect:/login";
        }


        // =====================================================
        // VALIDATE INPUT
        // =====================================================

        if (fullName == null
                || fullName.trim().isEmpty()
                || phoneNumber == null
                || phoneNumber.trim().isEmpty()
                || address == null
                || address.trim().isEmpty()) {


            redirectAttributes.addFlashAttribute(
                    "profileError",
                    "Full Name, Phone Number and Address are required."
            );


            return "redirect:/victim/dashboard";
        }


        // =====================================================
        // CREATE UPDATED VICTIM
        // =====================================================

        Victim updatedVictim =
                new Victim();


        updatedVictim.setFullName(
                fullName.trim()
        );


        updatedVictim.setPhoneNumber(
                phoneNumber.trim()
        );


        updatedVictim.setAddress(
                address.trim()
        );


        /*
         * NIC is NOT editable.
         *
         * The existing NIC is kept because
         * NIC is used as the victim's login username.
         */

        updatedVictim.setNicNumber(
                victim.getNicNumber()
        );


        // =====================================================
        // UPDATE DATABASE
        // =====================================================

        victimService.updateVictim(
                victimId,
                updatedVictim
        );


        // =====================================================
        // SUCCESS MESSAGE
        // =====================================================

        redirectAttributes.addFlashAttribute(
                "profileSuccess",
                "Your profile has been updated successfully."
        );


        return "redirect:/victim/dashboard";
    }
}