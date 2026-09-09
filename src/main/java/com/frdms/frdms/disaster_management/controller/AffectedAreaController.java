package com.frdms.frdms.disaster_management.controller;

import com.frdms.frdms.disaster_management.entity.AffectedArea;
import com.frdms.frdms.disaster_management.entity.Disaster;
import com.frdms.frdms.disaster_management.service.AffectedAreaService;
import com.frdms.frdms.disaster_management.service.DisasterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/affected-areas")
public class AffectedAreaController {

    private final AffectedAreaService affectedAreaService;
    private final DisasterService disasterService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AffectedAreaController(
            AffectedAreaService affectedAreaService,
            DisasterService disasterService) {

        this.affectedAreaService = affectedAreaService;
        this.disasterService = disasterService;
    }


    // =========================================================
    // DM-03
    // ADD AFFECTED AREA FORM
    // =========================================================

    @GetMapping("/new/{disasterId}")
    public String showCreateForm(
            @PathVariable Long disasterId,
            Model model) {

        // Find the flood event
        Disaster disaster =
                disasterService.getDisasterById(disasterId);

        // Create a new affected area
        AffectedArea affectedArea =
                new AffectedArea();

        // Connect affected area to flood event
        affectedArea.setDisaster(disaster);

        // Send data to Thymeleaf
        model.addAttribute(
                "affectedArea",
                affectedArea
        );

        model.addAttribute(
                "disaster",
                disaster
        );

        return "affected-area/form";
    }


    // =========================================================
    // DM-03
    // SAVE AFFECTED AREA
    // =========================================================

    @PostMapping("/save/{disasterId}")
    public String createAffectedArea(
            @PathVariable Long disasterId,
            @Valid @ModelAttribute("affectedArea")
            AffectedArea affectedArea,
            BindingResult bindingResult,
            Model model) {

        // Find the flood event
        Disaster disaster =
                disasterService.getDisasterById(disasterId);

        // =====================================================
        // VALIDATION
        // =====================================================

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "disaster",
                    disaster
            );

            // Keep relationship during validation
            affectedArea.setDisaster(disaster);

            return "affected-area/form";
        }


        // =====================================================
        // CONNECT AREA TO FLOOD EVENT
        // =====================================================

        affectedArea.setDisaster(disaster);


        // =====================================================
        // SAVE
        // =====================================================

        affectedAreaService.createAffectedArea(
                affectedArea
        );


        // =====================================================
        // RETURN TO FLOOD EVENT DETAILS
        // =====================================================

        return "redirect:/disasters/details/" + disasterId;
    }


    // =========================================================
    // DM-04
    // EDIT AFFECTED AREA FORM
    // =========================================================

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        // Find affected area
        AffectedArea affectedArea =
                affectedAreaService.getAffectedAreaById(id);

        // Find the related flood event
        Disaster disaster =
                affectedArea.getDisaster();

        // Send data to Thymeleaf
        model.addAttribute(
                "affectedArea",
                affectedArea
        );

        model.addAttribute(
                "disaster",
                disaster
        );

        return "affected-area/edit";
    }


    // =========================================================
    // DM-04
    // UPDATE AFFECTED AREA
    // =========================================================

    @PostMapping("/update/{id}")
    public String updateAffectedArea(
            @PathVariable Long id,
            @Valid @ModelAttribute("affectedArea")
            AffectedArea affectedArea,
            BindingResult bindingResult,
            Model model) {

        // Get existing affected area
        AffectedArea existingArea =
                affectedAreaService.getAffectedAreaById(id);

        // Get original flood event
        Disaster disaster =
                existingArea.getDisaster();


        // =====================================================
        // VALIDATION
        // =====================================================

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "disaster",
                    disaster
            );

            // Keep original relationship
            affectedArea.setDisaster(disaster);

            return "affected-area/edit";
        }


        // =====================================================
        // KEEP RELATIONSHIP
        // =====================================================

        affectedArea.setDisaster(disaster);


        // =====================================================
        // UPDATE
        // =====================================================

        affectedAreaService.updateAffectedArea(
                id,
                affectedArea
        );


        // =====================================================
        // RETURN TO FLOOD EVENT DETAILS
        // =====================================================

        Long disasterId =
                disaster.getId();

        return "redirect:/disasters/details/" + disasterId;
    }
}