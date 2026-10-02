package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.entity.GNReport;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;
import com.frdms.frdms.victim_management.service.GNReportService;
import com.frdms.frdms.victim_management.service.VictimService;

import com.frdms.frdms.shelter_management.entity.ShelterAllocation;
import com.frdms.frdms.shelter_management.service.ShelterAllocationService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VictimDashboardController {

    private final VictimService victimService;
    private final GNReportService gnReportService;
    private final AffectedFamilyInformationService
            affectedFamilyInformationService;
    private final ShelterAllocationService
            shelterAllocationService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimDashboardController(
            VictimService victimService,
            GNReportService gnReportService,
            AffectedFamilyInformationService
                    affectedFamilyInformationService,
            ShelterAllocationService
                    shelterAllocationService) {

        this.victimService = victimService;
        this.gnReportService = gnReportService;
        this.affectedFamilyInformationService =
                affectedFamilyInformationService;
        this.shelterAllocationService =
                shelterAllocationService;
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
                (Long) session.getAttribute("victimId");

        if (victimId == null) {
            return "redirect:/login";
        }


        // =====================================================
        // GET LOGGED-IN VICTIM
        // =====================================================

        Victim victim =
                victimService.getVictimById(victimId);


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
                gnReportService.getLatestGNReportByVictim(victim);


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

        ShelterAllocation shelterAllocation = null;

        if (familyInformation != null
                && Boolean.TRUE.equals(
                familyInformation.getShelterRequired())) {

            if (shelterAllocationService.isFamilyAllocated(
                    familyInformation.getId())) {

                shelterAllocation =
                        shelterAllocationService
                                .getAllocationByFamilyId(
                                        familyInformation.getId()
                                );
            }
        }


        // =====================================================
        // SEND VICTIM INFORMATION TO DASHBOARD
        // =====================================================

        model.addAttribute(
                "victim",
                victim
        );


        // =====================================================
        // SEND GN REPORT INFORMATION TO DASHBOARD
        // =====================================================

        model.addAttribute(
                "gnReport",
                gnReport
        );


        // =====================================================
        // SEND FAMILY INFORMATION TO DASHBOARD
        // =====================================================

        model.addAttribute(
                "familyInformation",
                familyInformation
        );


        // =====================================================
        // SEND SHELTER ALLOCATION TO DASHBOARD
        // =====================================================

        model.addAttribute(
                "shelterAllocation",
                shelterAllocation
        );


        // =====================================================
        // OPEN VICTIM DASHBOARD
        // =====================================================

        return "victim/dashboard";
    }
}