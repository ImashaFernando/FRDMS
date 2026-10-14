
package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.distribution_management.entity.ReliefPackage;
import com.frdms.frdms.distribution_management.repository.ReliefPackageRepository;

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
    private final AffectedFamilyInformationService affectedFamilyInformationService;
    private final ShelterAllocationService shelterAllocationService;
    private final ReliefRequirementService reliefRequirementService;
    private final NeedsAssessmentService needsAssessmentService;
    private final ReliefPackageRepository reliefPackageRepository;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimDashboardController(
            VictimService victimService,
            GNReportService gnReportService,
            AffectedFamilyInformationService affectedFamilyInformationService,
            ShelterAllocationService shelterAllocationService,
            ReliefRequirementService reliefRequirementService,
            NeedsAssessmentService needsAssessmentService,
            ReliefPackageRepository reliefPackageRepository) {

        this.victimService = victimService;
        this.gnReportService = gnReportService;
        this.affectedFamilyInformationService = affectedFamilyInformationService;
        this.shelterAllocationService = shelterAllocationService;
        this.reliefRequirementService = reliefRequirementService;
        this.needsAssessmentService = needsAssessmentService;
        this.reliefPackageRepository = reliefPackageRepository;
    }

    // =========================================================
    // VICTIM DASHBOARD
    // =========================================================

    @GetMapping("/victim/dashboard")
    public String showDashboard(HttpSession session, Model model) {

        // Check victim login
        Long victimId = (Long) session.getAttribute("victimId");

        if (victimId == null) {
            return "redirect:/login";
        }

        // Get logged-in victim
        Victim victim;

        try {
            victim = victimService.getVictimById(victimId);
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login";
        }

        if (victim == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // Get latest GN report
        GNReport gnReport =
                gnReportService.getLatestGNReportByVictim(victim);

        // Get family information
        AffectedFamilyInformation familyInformation =
                affectedFamilyInformationService.getByVictim(victim)
                        .orElse(null);

        // Get shelter allocation
        ShelterAllocation shelterAllocation = null;

        if (familyInformation != null
                && Boolean.TRUE.equals(familyInformation.getShelterRequired())) {

            if (shelterAllocationService.isFamilyAllocated(
                    familyInformation.getId())) {

                shelterAllocation =
                        shelterAllocationService.getAllocationByFamilyId(
                                familyInformation.getId());
            }
        }

        // Get latest needs assessment
        NeedsAssessment needsAssessment =
                needsAssessmentService.getLatestAssessmentByVictim(victim);

        // Get relief requirements belonging to this victim
        List<ReliefRequirement> reliefRequirements =
                reliefRequirementService.getByVictim(victim);

        // =====================================================
        // GET THIS VICTIM'S RELIEF PACKAGE STATUSES
        // =====================================================

        List<ReliefPackage> reliefPackages = List.of();

        if (reliefRequirements != null && !reliefRequirements.isEmpty()) {

            List<Long> requirementIds = reliefRequirements.stream()
                    .map(ReliefRequirement::getId)
                    .filter(id -> id != null)
                    .toList();

            if (!requirementIds.isEmpty()) {
                reliefPackages =
                        reliefPackageRepository.findByRequirementIdIn(
                                requirementIds);
            }
        }

        // Get latest relief requirement
        ReliefRequirement latestReliefRequirement = null;

        if (reliefRequirements != null && !reliefRequirements.isEmpty()) {
            latestReliefRequirement =
                    reliefRequirements.get(reliefRequirements.size() - 1);
        }

        // Send information to dashboard
        model.addAttribute("victim", victim);
        model.addAttribute("gnReport", gnReport);
        model.addAttribute("familyInformation", familyInformation);
        model.addAttribute("shelterAllocation", shelterAllocation);
        model.addAttribute("needsAssessment", needsAssessment);
        model.addAttribute("reliefRequirements", reliefRequirements);
        model.addAttribute("latestReliefRequirement", latestReliefRequirement);

        // Send package status information to dashboard
        model.addAttribute("reliefPackages", reliefPackages);

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

        // Check victim login
        Long victimId = (Long) session.getAttribute("victimId");

        if (victimId == null) {
            return "redirect:/login";
        }

        // Get logged-in victim
        Victim victim;

        try {
            victim = victimService.getVictimById(victimId);
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login";
        }

        if (victim == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // Validate input
        if (fullName == null || fullName.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()
                || address == null || address.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "profileError",
                    "Full Name, Phone Number and Address are required.");

            return "redirect:/victim/dashboard";
        }

        // Create updated victim
        Victim updatedVictim = new Victim();

        updatedVictim.setFullName(fullName.trim());
        updatedVictim.setPhoneNumber(phoneNumber.trim());
        updatedVictim.setAddress(address.trim());

        // Keep the existing NIC because it is the login username
        updatedVictim.setNicNumber(victim.getNicNumber());

        // Update database
        victimService.updateVictim(victimId, updatedVictim);

        // Success message
        redirectAttributes.addFlashAttribute(
                "profileSuccess",
                "Your profile has been updated successfully.");

        return "redirect:/victim/dashboard";
    }
}
