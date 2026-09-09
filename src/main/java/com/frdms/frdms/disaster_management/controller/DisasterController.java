package com.frdms.frdms.disaster_management.controller;

import com.frdms.frdms.disaster_management.entity.Disaster;
import com.frdms.frdms.disaster_management.service.AffectedAreaService;
import com.frdms.frdms.disaster_management.service.DisasterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/disasters")
public class DisasterController {

    private final DisasterService disasterService;
    private final AffectedAreaService affectedAreaService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DisasterController(
            DisasterService disasterService,
            AffectedAreaService affectedAreaService) {

        this.disasterService = disasterService;
        this.affectedAreaService = affectedAreaService;
    }


    // =========================================================
    // DM-01
    // CREATE FLOOD EVENT FORM
    // =========================================================

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        model.addAttribute(
                "disaster",
                new Disaster()
        );

        return "disaster/form";
    }


    // =========================================================
    // DM-01 / DM-02
    // SAVE FLOOD EVENT
    // =========================================================

    @PostMapping
    public String createDisaster(
            @Valid @ModelAttribute("disaster")
            Disaster disaster,
            BindingResult bindingResult,
            Model model) {

        // Check validation errors
        if (bindingResult.hasErrors()) {

            return "disaster/form";
        }

        // Save flood event
        disasterService.createDisaster(disaster);

        // Go to flood event list
        return "redirect:/disasters";
    }


    // =========================================================
    // VIEW ALL FLOOD EVENTS
    // =========================================================

    @GetMapping
    public String listDisasters(Model model) {

        model.addAttribute(
                "disasters",
                disasterService.getAllDisasters()
        );

        return "disaster/list";
    }


    // =========================================================
    // DM-02 / DM-03 / DM-04
    // VIEW FLOOD EVENT DETAILS
    // =========================================================

    @GetMapping("/details/{id}")
    public String showDisasterDetails(
            @PathVariable Long id,
            Model model) {

        // Find the selected flood event
        Disaster disaster =
                disasterService.getDisasterById(id);

        // Find affected areas belonging to this flood event
        var affectedAreas =
                affectedAreaService
                        .getAffectedAreasByDisaster(disaster);

        // Send flood event to Thymeleaf
        model.addAttribute(
                "disaster",
                disaster
        );

        // Send affected areas to Thymeleaf
        model.addAttribute(
                "affectedAreas",
                affectedAreas
        );

        return "disaster/details";
    }


    // =========================================================
    // EDIT FLOOD EVENT FORM
    // =========================================================

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        Disaster disaster =
                disasterService.getDisasterById(id);

        model.addAttribute(
                "disaster",
                disaster
        );

        return "disaster/edit";
    }


    // =========================================================
    // UPDATE FLOOD EVENT
    // =========================================================

    @PostMapping("/update/{id}")
    public String updateDisaster(
            @PathVariable Long id,
            @Valid @ModelAttribute("disaster")
            Disaster disaster,
            BindingResult bindingResult) {

        // Get original flood event
        Disaster existingDisaster =
                disasterService.getDisasterById(id);


        // =====================================================
        // PRESERVE ORIGINAL EVENT DATE
        // =====================================================

        /*
         * The event date is read-only in the edit form.
         * Therefore, if no date is submitted, use the
         * original date stored in the database.
         */

        if (disaster.getEventDate() == null) {

            disaster.setEventDate(
                    existingDisaster.getEventDate()
            );
        }


        // =====================================================
        // VALIDATION
        // =====================================================

        if (bindingResult.hasErrors()) {

            return "disaster/edit";
        }


        // =====================================================
        // UPDATE FLOOD EVENT
        // =====================================================

        disasterService.updateDisaster(
                id,
                disaster
        );


        // Return to flood event list
        return "redirect:/disasters";
    }
}