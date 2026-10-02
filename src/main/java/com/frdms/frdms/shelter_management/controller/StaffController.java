package com.frdms.frdms.shelter_management.controller;

import com.frdms.frdms.shelter_management.service.ShelterAllocationService;
import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StaffController {

    private final AffectedFamilyInformationService
            affectedFamilyInformationService;

    private final ShelterAllocationService
            shelterAllocationService;

    public StaffController(
            AffectedFamilyInformationService affectedFamilyInformationService,
            ShelterAllocationService shelterAllocationService
    ) {
        this.affectedFamilyInformationService =
                affectedFamilyInformationService;

        this.shelterAllocationService =
                shelterAllocationService;
    }


    // =========================================================
    // STAFF LOGIN
    // =========================================================

    @GetMapping("/staff/login")
    public String staffLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {

        if (error != null) {
            model.addAttribute(
                    "errorMessage",
                    "Invalid email or password."
            );
        }

        if (logout != null) {
            model.addAttribute(
                    "logoutMessage",
                    "You have been logged out successfully."
            );
        }

        return "login";
    }


    // =========================================================
    // STAFF DASHBOARD
    // =========================================================

    @GetMapping("/staff/dashboard")
    public String staffDashboard(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "staffEmail",
                authentication.getName()
        );

        return "shelter/staff-dashboard";
    }


    // =========================================================
    // ELIGIBLE FAMILIES
    // SM-US7
    // =========================================================

    @GetMapping("/staff/eligible-families")
    public String viewEligibleFamilies(
            Model model
    ) {

        List<AffectedFamilyInformation> eligibleFamilies =
                affectedFamilyInformationService
                        .getFamiliesRequiringShelter();

        model.addAttribute(
                "eligibleFamilies",
                eligibleFamilies
        );


        // =====================================================
        // CHECK ALLOCATION STATUS OF EACH FAMILY
        // =====================================================

        Map<Long, Boolean> allocationStatus =
                new HashMap<>();

        for (AffectedFamilyInformation family : eligibleFamilies) {

            allocationStatus.put(
                    family.getId(),
                    shelterAllocationService
                            .isFamilyAllocated(family.getId())
            );
        }

        model.addAttribute(
                "allocationStatus",
                allocationStatus
        );


        return "shelter/eligible-families";
    }
}