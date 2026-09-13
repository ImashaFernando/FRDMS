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
@RequestMapping("/victim/family-information")
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
    // SHOW FAMILY & AFFECTED INFORMATION FORM
    // =========================================================

    @GetMapping
    public String showForm(
            HttpSession session,
            Model model) {

        // Get logged-in victim ID from session
        Long victimId =
                (Long) session.getAttribute("victimId");

        // Victim must be logged in
        if (victimId == null) {
            return "redirect:/victim-login";
        }

        // Get the logged-in victim
        Victim victim =
                victimService.getVictimById(victimId);

        /*
         * Check whether this victim has already
         * submitted Family & Affected Information.
         */
        Optional<AffectedFamilyInformation> existingInformation =
                informationService.getByVictim(victim);


        if (existingInformation.isPresent()) {

            /*
             * Existing information is loaded into
             * the form so the victim can view
             * and edit the saved information.
             */
            model.addAttribute(
                    "information",
                    existingInformation.get()
            );

        } else {

            /*
             * First visit:
             * Create an empty form.
             */
            AffectedFamilyInformation information =
                    new AffectedFamilyInformation();

            model.addAttribute(
                    "information",
                    information
            );
        }


        // Send victim information to the page
        model.addAttribute(
                "victim",
                victim
        );


        return "victim/family-information";
    }


    // =========================================================
    // SAVE / UPDATE FAMILY & AFFECTED INFORMATION
    // =========================================================

    @PostMapping
    public String saveInformation(
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
        // VALIDATION ERRORS
        // =====================================================

        if (bindingResult.hasErrors()) {

            /*
             * Stay on the same page when validation
             * errors occur.
             */
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


            /*
             * Success notification for UPDATE
             */
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your Family & Affected Information has been updated successfully."
            );

        }


        // =====================================================
        // SAVE NEW INFORMATION
        // =====================================================

        else {

            /*
             * Connect the submitted information
             * with the logged-in victim.
             */
            information.setVictim(victim);


            informationService.saveInformation(
                    information
            );


            /*
             * Success notification for FIRST SAVE
             */
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your Family & Affected Information has been saved successfully."
            );
        }


        // =====================================================
        // RETURN TO THE SAME FORM PAGE
        // =====================================================

        return "redirect:/victim/family-information";
    }
}