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

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AffectedFamilyInformationController(
            AffectedFamilyInformationService informationService,
            VictimService victimService) {

        this.informationService = informationService;
        this.victimService = victimService;
    }


    // =========================================================
    // VICTIM SIDE
    // SHOW FAMILY & AFFECTED INFORMATION FORM
    // =========================================================

    @GetMapping("/victim/family-information")
    public String showVictimFamilyInformationForm(
            HttpSession session,
            Model model) {

        Long victimId = (Long) session.getAttribute("victimId");

        // Victim must be logged in
        if (victimId == null) {
            return "redirect:/login";
        }

        // =====================================================
        // GET VICTIM
        // getVictimById() returns Victim
        // =====================================================

        Victim victim = victimService.getVictimById(victimId);

        // Victim not found
        if (victim == null) {
            session.invalidate();
            return "redirect:/login";
        }


        // =====================================================
        // CHECK EXISTING FAMILY INFORMATION
        // =====================================================

        Optional<AffectedFamilyInformation> existingInformation =
                informationService.getByVictim(victim);


        // =====================================================
        // EXISTING INFORMATION
        // =====================================================

        if (existingInformation.isPresent()) {

            model.addAttribute(
                    "information",
                    existingInformation.get()
            );

        } else {

            // =================================================
            // FIRST-TIME SUBMISSION
            // =================================================

            AffectedFamilyInformation information =
                    new AffectedFamilyInformation();

            information.setVictim(victim);

            model.addAttribute(
                    "information",
                    information
            );
        }


        // =====================================================
        // ADD VICTIM TO MODEL
        // =====================================================

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

            @Valid
            @ModelAttribute("information")
            AffectedFamilyInformation information,

            BindingResult bindingResult,

            HttpSession session,

            Model model,

            RedirectAttributes redirectAttributes) {


        // =====================================================
        // GET LOGGED-IN VICTIM ID
        // =====================================================

        Long victimId =
                (Long) session.getAttribute("victimId");


        // =====================================================
        // VICTIM MUST BE LOGGED IN
        // =====================================================

        if (victimId == null) {
            return "redirect:/login";
        }


        // =====================================================
        // GET VICTIM
        // =====================================================

        Victim victim =
                victimService.getVictimById(victimId);


        // Victim no longer exists
        if (victim == null) {

            session.invalidate();

            return "redirect:/login";
        }


        // =====================================================
        // ADD VICTIM TO MODEL
        // =====================================================

        model.addAttribute(
                "victim",
                victim
        );


        // =====================================================
        // VALIDATION
        // =====================================================

        if (bindingResult.hasErrors()) {

            // Maintain relationship
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


        // =====================================================
        // RETURN TO FAMILY INFORMATION PAGE
        // =====================================================

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


        // =====================================================
        // GET SELECTED VICTIM
        // =====================================================

        Victim victim =
                victimService.getVictimById(victimId);


        // =====================================================
        // VICTIM NOT FOUND
        // =====================================================

        if (victim == null) {
            return "redirect:/victims";
        }


        // =====================================================
        // GET FAMILY INFORMATION
        // =====================================================

        Optional<AffectedFamilyInformation> existingInformation =
                informationService.getByVictim(victim);


        // =====================================================
        // ADD VICTIM
        // =====================================================

        model.addAttribute(
                "victim",
                victim
        );


        // =====================================================
        // ADD FAMILY INFORMATION
        // =====================================================

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

            @Valid
            @ModelAttribute("information")
            AffectedFamilyInformation information,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {


        // =====================================================
        // GET SELECTED VICTIM
        // =====================================================

        Victim victim =
                victimService.getVictimById(victimId);


        // =====================================================
        // VICTIM NOT FOUND
        // =====================================================

        if (victim == null) {
            return "redirect:/victims";
        }


        // =====================================================
        // ADD VICTIM TO MODEL
        // =====================================================

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
        // CREATE NEW INFORMATION
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


        // =====================================================
        // RETURN TO SELECTED VICTIM
        // =====================================================

        return "redirect:/victims/" + victimId;
    }
}