package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/relief-coordinator")
public class ReliefCoordinatorController {

    private final AffectedFamilyInformationService informationService;

    public ReliefCoordinatorController(
            AffectedFamilyInformationService informationService) {

        this.informationService = informationService;
    }


    // =========================================================
    // VIEW ALL AFFECTED FAMILY INFORMATION
    // =========================================================

    @GetMapping("/affected-families")
    public String viewAffectedFamilies(Model model) {

        model.addAttribute(
                "informationList",
                informationService.getAllInformation()
        );

        return "relief-coordinator/affected-families";
    }


    // =========================================================
    // VIEW DETAILS OF ONE AFFECTED FAMILY
    // =========================================================

    @GetMapping("/affected-families/{id}")
    public String viewAffectedFamilyDetails(
            @PathVariable Long id,
            Model model) {

        AffectedFamilyInformation information =
                informationService.getById(id);

        model.addAttribute(
                "information",
                information
        );

        return "relief-coordinator/affected-family-details";
    }
}