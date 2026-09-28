package com.frdms.frdms.shelter_management.controller;

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


    // =========================
    // VIEW ONE SHELTER
    // =========================

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


    // =========================
    // SM-US4
    // VIEW ALL SHELTERS
    // =========================

    @GetMapping("/staff/shelters")
    public String viewShelters(Model model) {

        model.addAttribute(
                "shelters",
                shelterService.getAllShelters()
        );

        return "shelter/shelter-list";
    }


    // =========================
    // SHOW EDIT SHELTER FORM
    // =========================

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

        return "shelter/shelter-edit";
    }


    // =========================
    // SM-US5 / SM-US10
    // UPDATE SHELTER DETAILS
    // Contact Person
    // Contact Number
    // Current Occupancy
    // Status
    // =========================

    @PostMapping("/staff/shelters/{id}/update")
    public String updateShelterDetails(
            @PathVariable("id") Long id,
            @RequestParam("contactPerson") String contactPerson,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam(value = "currentOccupancy", required = false) Integer currentOccupancy,
            @RequestParam("status") Shelter.ShelterStatus status,
            RedirectAttributes redirectAttributes
    ) {

        boolean hasErrors = false;

        if (contactPerson == null || contactPerson.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "contactPersonError",
                    "Contact person cannot be empty."
            );

            hasErrors = true;
        }

        if (contactNumber == null || contactNumber.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "contactNumberError",
                    "Contact number cannot be empty."
            );

            hasErrors = true;

        } else if (!contactNumber.matches("^0\\d{9}$")) {

            redirectAttributes.addFlashAttribute(
                    "contactNumberError",
                    "Contact number must be a valid 10-digit number starting with 0."
            );

            hasErrors = true;
        }

        if (currentOccupancy == null) {

            redirectAttributes.addFlashAttribute(
                    "currentOccupancyError",
                    "Current occupancy is required."
            );

            hasErrors = true;

        } else if (currentOccupancy < 0) {

            redirectAttributes.addFlashAttribute(
                    "currentOccupancyError",
                    "Current occupancy cannot be negative."
            );

            hasErrors = true;
        }

        if (hasErrors) {

            redirectAttributes.addFlashAttribute(
                    "submittedContactPerson",
                    contactPerson
            );

            redirectAttributes.addFlashAttribute(
                    "submittedContactNumber",
                    contactNumber
            );

            redirectAttributes.addFlashAttribute(
                    "submittedCurrentOccupancy",
                    currentOccupancy
            );

            redirectAttributes.addFlashAttribute(
                    "submittedStatus",
                    status
            );

            return "redirect:/staff/shelters/" + id + "/edit";
        }

        try {

            Shelter updatedShelter =
                    shelterService.updateShelterDetails(
                            id,
                            contactPerson,
                            contactNumber,
                            currentOccupancy,
                            status
                    );

            if (updatedShelter.getStatus() != status) {

                redirectAttributes.addFlashAttribute(
                        "warningMessage",
                        "Shelter details updated, but status was automatically set to FULL because current occupancy has reached maximum capacity."
                );

            } else {

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Shelter details updated successfully."
                );
            }

            return "redirect:/staff/shelters/" + id;

        } catch (IllegalArgumentException exception) {

            String errorMessage = exception.getMessage();

            redirectAttributes.addFlashAttribute(
                    "submittedContactPerson",
                    contactPerson
            );

            redirectAttributes.addFlashAttribute(
                    "submittedContactNumber",
                    contactNumber
            );

            redirectAttributes.addFlashAttribute(
                    "submittedCurrentOccupancy",
                    currentOccupancy
            );

            redirectAttributes.addFlashAttribute(
                    "submittedStatus",
                    status
            );

            if (errorMessage.toLowerCase().contains("occupancy")) {

                redirectAttributes.addFlashAttribute(
                        "currentOccupancyError",
                        errorMessage
                );

            } else if (errorMessage.toLowerCase().contains("status")) {

                redirectAttributes.addFlashAttribute(
                        "statusError",
                        errorMessage
                );

            } else {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        errorMessage
                );
            }

            return "redirect:/staff/shelters/" + id + "/edit";
        }
    }


    // =========================
    // DELETE SHELTER
    // =========================

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
