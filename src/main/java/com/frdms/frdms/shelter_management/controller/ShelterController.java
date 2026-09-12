package com.frdms.frdms.shelter_management.controller;

import com.frdms.frdms.shelter_management.entity.Shelter;
import com.frdms.frdms.shelter_management.service.ShelterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(
            ShelterService shelterService
    ) {
        this.shelterService = shelterService;
    }

    // =========================
    // SM-US1
    // SHOW REGISTER SHELTER FORM
    // =========================

    @GetMapping("/staff/shelters/register")
    public String showRegisterShelterForm(Model model) {

        model.addAttribute(
                "shelter",
                new Shelter()
        );

        return "shelter/shelter-form";
    }


    // =========================
    // SM-US1
    // REGISTER SHELTER
    // =========================

    @PostMapping("/staff/shelters/register")
    public String registerShelter(
            @Valid Shelter shelter,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            return "shelter/shelter-form";
        }

        try {

            shelterService.registerShelter(shelter);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Shelter registered successfully."
            );

            return "redirect:/staff/shelters/register";

        } catch (IllegalArgumentException exception) {

            bindingResult.reject(
                    "shelterError",
                    exception.getMessage()
            );

            return "shelter/shelter-form";
        }
    }
    @GetMapping("/staff/shelters/{id}")
    public String viewShelterDetails(
            @PathVariable("id") Long id,
            Model model
    ) {

        Shelter shelter =
                shelterService.getShelterById(id);

        model.addAttribute(
                "shelter",
                shelter
        );

        return "shelter/shelter-view";
    }
    @GetMapping("/staff/shelters")
    public String viewShelters(Model model) {

        model.addAttribute(
                "shelters",
                shelterService.getAllShelters()
        );

        return "shelter/shelter-list";
    }
    // =========================
// SM-US10
// UPDATE SHELTER STATUS
// =========================

    @PostMapping("/staff/shelters/{id}/status")
    public String updateShelterStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Shelter.ShelterStatus status,
            RedirectAttributes redirectAttributes
    ) {

        Shelter updatedShelter =
                shelterService.updateShelterStatus(id, status);

        if (updatedShelter.getStatus() != status) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Status cannot be changed because the shelter is currently at maximum capacity. Status remains FULL."
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Shelter status updated successfully."
            );
        }

        return "redirect:/staff/shelters/" + id;
    }
}

