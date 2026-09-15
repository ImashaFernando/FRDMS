package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;
import com.frdms.frdms.victim_management.service.VictimService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AffectedFamilyInformationController {

    private final AffectedFamilyInformationService informationService;
    private final VictimService victimService;

    public AffectedFamilyInformationController(
            AffectedFamilyInformationService informationService,
            VictimService victimService) {

        this.informationService = informationService;
        this.victimService = victimService;
    }


    // =========================================================
    // VICTIM SIDE
    // VIEW FAMILY & AFFECTED INFORMATION FORM
    // =========================================================

    @GetMapping("/victim/family-information")
    public String showVictimFamilyInformationForm(
            HttpSession session,
            Model model) {

        // Get logged-in victim ID
        Long victimId =
                (Long) session.getAttribute("victimId");

        // Victim must be logged in
        if (victimId == null) {
            return "redirect:/victim-login";
        }

        // Get logged-in victim
        Victim victim =
                victimService.getVictimById(victimId);

        // Check whether information already exists
        Optional<AffectedFamilyInformation> existingInformation =
                informationService.getByVictim(victim);


        if (existingInformation.isPresent()) {

            // Load existing information for viewing/editing
            model.addAttribute(
                    "information",
                    existingInformation.get()
            );

        } else {

            // Create empty form for first submission
            AffectedFamilyInformation information =
                    new AffectedFamilyInformation();

            information.setVictim(victim);

            model.addAttribute(
                    "information",
                    information
            );
        }


        model.addAttribute(
                "victim",
                victim
        );


        return "victim/family-information";
    }


    // =========================================================
    // VICTIM SIDE
    // SAVE / UPDATE FAMILY & AFFECTED INFORMATION
    // =========================================================

    @PostMapping("/victim/family-information")
    public String saveVictimFamilyInformation(
            @Valid @ModelAttribute("information")
            AffectedFamilyInformation information,

            BindingResult bindingResult,

            HttpSession session,

            Model model,

            RedirectAttributes redirectAttributes) {

        // Get logged-in victim ID
        Long victimId =
                (Long) session.getAttribute("victimId");

        // Victim must be logged in
        if (victimId == null) {
            return "redirect:/victim-login";
        }

        // Get logged-in victim
        Victim victim =
                victimService.getVictimById(victimId);

        model.addAttribute(
                "victim",
                victim
        );


        // =====================================================
        // VALIDATION
        // =====================================================

        if (bindingResult.hasErrors()) {

            information.setVictim(victim);

            return "victim/family-information";
        }


        // =====================================================
        // CHECK EXISTING INFORMATION
        // =====================================================

        Optional<AffectedFamilyInformation> existingInformation =
                informationService.getByVictim(victim);


        // =====================================================
        // UPDATE EXISTING INFORMATION
        // =====================================================

        if (existingInformation.isPresent()) {

            AffectedFamilyInformation existing =
                    existingInformation.get();

            informationService.updateInformation(
                    existing.getId(),
                    information
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your Family & Affected Information has been updated successfully."
            );
        }


        // =====================================================
        // SAVE NEW INFORMATION
        // =====================================================

        else {

            information.setVictim(victim);

            informationService.saveInformation(
                    information
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your Family & Affected Information has been submitted successfully."
            );
        }


        return "redirect:/victim/family-information";
    }


    // =========================================================
    // PROJECT MANAGER SIDE
    // VIEW FAMILY & AFFECTED INFORMATION
    // FOR A SELECTED VICTIM
    // =========================================================

    @GetMapping("/project-manager/victims/{victimId}/family-information")
    public String showPMFamilyInformation(
            @PathVariable Long victimId,
            Model model) {

        // Get selected victim
        Victim victim =
                victimService.getVictimById(victimId);

        // Get family information
        Optional<AffectedFamilyInformation> existingInformation =
                informationService.getByVictim(victim);


        /*
         * The PM can see whether the victim has
         * submitted Family & Affected Information.
         */

        model.addAttribute(
                "victim",
                victim
        );

        model.addAttribute(
                "information",
                existingInformation.orElse(null)
        );


        return "project-manager/family-information";
    }


    // =========================================================
    // PROJECT MANAGER SIDE
    // RECORD / UPDATE FAMILY & AFFECTED INFORMATION
    // =========================================================

    @PostMapping("/project-manager/victims/{victimId}/family-information")
    public String savePMFamilyInformation(
            @PathVariable Long victimId,

            @Valid @ModelAttribute("information")
            AffectedFamilyInformation information,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        // Get selected victim
        Victim victim =
                victimService.getVictimById(victimId);

        model.addAttribute(
                "victim",
                victim
        );


        // =====================================================
        // VALIDATION
        // =====================================================

        if (bindingResult.hasErrors()) {

            information.setVictim(victim);

            return "project-manager/family-information";
        }


        // =====================================================
        // CHECK EXISTING INFORMATION
        // =====================================================

        Optional<AffectedFamilyInformation> existingInformation =
                informationService.getByVictim(victim);


        // =====================================================
        // UPDATE EXISTING INFORMATION
        // =====================================================

        if (existingInformation.isPresent()) {

            AffectedFamilyInformation existing =
                    existingInformation.get();

            informationService.updateInformation(
                    existing.getId(),
                    information
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Family & Affected Information has been updated successfully."
            );
        }


        // =====================================================
        // CREATE INFORMATION IF NOT YET SUBMITTED
        // =====================================================

        else {

            information.setVictim(victim);

            informationService.saveInformation(
                    information
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Family & Affected Information has been recorded successfully."
            );
        }


        // Return to selected victim details
        return "redirect:/victims/" + victimId;
    }
}