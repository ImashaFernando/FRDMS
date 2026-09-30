package com.frdms.frdms.shelter_management.controller;

import com.frdms.frdms.disaster_management.service.DisasterService;
import com.frdms.frdms.shelter_management.entity.Shelter;
import com.frdms.frdms.shelter_management.service.ShelterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ShelterController {

    private final ShelterService shelterService;
    private final DisasterService disasterService;

    public ShelterController(
            ShelterService shelterService,
            DisasterService disasterService
    ) {
        this.shelterService = shelterService;
        this.disasterService = disasterService;
    }

    @GetMapping("/staff/shelters/register")
    public String showRegisterShelterForm(Model model) {

        model.addAttribute(
                "shelter",
                new Shelter()
        );

        // Load disasters created from Disaster Management
        model.addAttribute(
                "disasters",
                disasterService.getAllDisasters()
        );

        return "shelter/shelter-form";
    }

    @PostMapping("/staff/shelters/register")
    public String registerShelter(
            @Valid Shelter shelter,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            // Reload disasters if validation fails
            model.addAttribute(
                    "disasters",
                    disasterService.getAllDisasters()
            );

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

            // Reload disasters if registration fails
            model.addAttribute(
                    "disasters",
                    disasterService.getAllDisasters()
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

    @GetMapping("/staff/shelters/{id}/edit")
    public String showEditShelterForm(
            @PathVariable("id") Long id,
            Model model
    ) {

        Shelter shelter =
                shelterService.getShelterById(id);

        model.addAttribute(
                "shelter",
                shelter
        );

        // Load disasters created from Disaster Management
        model.addAttribute(
                "disasters",
                disasterService.getAllDisasters()
        );

        return "shelter/shelter-edit";
    }

    @PostMapping("/staff/shelters/{id}/update")
    public String updateShelterDetails(
            @PathVariable("id") Long id,

            @RequestParam("contactPerson")
            String contactPerson,

            @RequestParam("contactNumber")
            String contactNumber,

            @RequestParam("disasterName")
            String disasterName,

            @RequestParam(
                    value = "currentOccupancy",
                    required = false
            )
            Integer currentOccupancy,

            @RequestParam("status")
            Shelter.ShelterStatus status,

            RedirectAttributes redirectAttributes
    ) {

        try {

            shelterService.updateShelterDetails(
                    id,
                    contactPerson,
                    contactNumber,
                    disasterName,
                    currentOccupancy,
                    status
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Shelter details updated successfully."
            );

            return "redirect:/staff/shelters/" + id;

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/staff/shelters/" + id + "/edit";
        }
    }

    @PostMapping("/staff/shelters/{id}/delete")
    public String deleteShelter(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            shelterService.deleteShelter(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Shelter deleted successfully."
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/staff/shelters";
    }
}