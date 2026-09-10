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
    // SHOW ADD AFFECTED AREA FORM
    // =========================================================

    @GetMapping("/new/{disasterId}")
    public String showCreateForm(
            @PathVariable Long disasterId,
            Model model) {

        // Find the selected disaster
        Disaster disaster =
                disasterService.getDisasterById(disasterId);

        // Create a new affected area
        AffectedArea affectedArea =
                new AffectedArea();

        // Connect the area to the disaster
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

        // Find the selected disaster
        Disaster disaster =
                disasterService.getDisasterById(disasterId);

        // =====================================================
        // VALIDATION
        // =====================================================

        if (bindingResult.hasErrors()) {

            // Keep the disaster relationship
            affectedArea.setDisaster(disaster);

            model.addAttribute(
                    "disaster",
                    disaster
            );

            return "affected-area/form";
        }


        // =====================================================
        // CONNECT AREA TO DISASTER
        // =====================================================

        affectedArea.setDisaster(disaster);


        // =====================================================
        // SAVE TO DATABASE
        // =====================================================

        affectedAreaService.createAffectedArea(
                affectedArea,
                disaster
        );


        // =====================================================
        // RETURN TO DISASTER DETAILS
        // =====================================================

        return "redirect:/disasters/details/"
                + disasterId;
    }


    // =========================================================
    // DM-04
    // SHOW EDIT AFFECTED AREA FORM
    // =========================================================

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        // Find affected area
        AffectedArea affectedArea =
                affectedAreaService.getAffectedAreaById(id);


        // Find the disaster belonging to this area
        Disaster disaster =
                affectedArea.getDisaster();


        // Send affected area to Thymeleaf
        model.addAttribute(
                "affectedArea",
                affectedArea
        );


        // Send disaster to Thymeleaf
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

        // =====================================================
        // GET EXISTING AFFECTED AREA
        // =====================================================

        AffectedArea existingArea =
                affectedAreaService.getAffectedAreaById(id);


        // =====================================================
        // GET ORIGINAL DISASTER
        // =====================================================

        Disaster disaster =
                existingArea.getDisaster();


        // =====================================================
        // VALIDATION
        // =====================================================

        if (bindingResult.hasErrors()) {

            // Keep original disaster relationship
            affectedArea.setDisaster(disaster);

            model.addAttribute(
                    "disaster",
                    disaster
            );

            return "affected-area/edit";
        }


        // =====================================================
        // KEEP ORIGINAL DISASTER RELATIONSHIP
        // =====================================================

        affectedArea.setDisaster(disaster);


        // =====================================================
        // UPDATE DATABASE
        // =====================================================

        affectedAreaService.updateAffectedArea(
                id,
                affectedArea
        );


        // =====================================================
        // RETURN TO DISASTER DETAILS
        // =====================================================

        return "redirect:/disasters/details/"
                + disaster.getId();
    }


    // =========================================================
    // DELETE AFFECTED AREA
    // =========================================================

    @PostMapping("/delete/{id}")
    public String deleteAffectedArea(
            @PathVariable Long id) {

        // Find affected area
        AffectedArea affectedArea =
                affectedAreaService.getAffectedAreaById(id);


        // Get related disaster ID
        Long disasterId =
                affectedArea.getDisaster().getId();


        // Delete affected area
        affectedAreaService.deleteAffectedArea(id);


        // Return to disaster details
        return "redirect:/disasters/details/"
                + disasterId;
    }
}