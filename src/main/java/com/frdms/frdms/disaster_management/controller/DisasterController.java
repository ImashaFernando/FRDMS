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
    public String showCreateForm(
            Model model) {

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

            BindingResult bindingResult) {


        // Check validation errors

        if (bindingResult.hasErrors()) {

            return "disaster/form";
        }


        // Save flood event

        disasterService.createDisaster(
                disaster
        );


        // Return to flood event list

        return "redirect:/disasters";
    }


    // =========================================================
    // VIEW / SEARCH FLOOD EVENTS
    // =========================================================

    @GetMapping
    public String listDisasters(

            @RequestParam(
                    required = false
            )
            String keyword,

            Model model) {


        /*
         * If a keyword is entered, matching flood events
         * are displayed.
         *
         * If the keyword is empty, all flood events
         * are displayed.
         */

        model.addAttribute(
                "disasters",
                disasterService.searchDisasters(keyword)
        );


        /*
         * Keep the keyword in the model so that the
         * search box can display what the user searched.
         */

        model.addAttribute(
                "keyword",
                keyword
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


        // Find all affected areas belonging
        // to this flood event

        var affectedAreas =
                affectedAreaService
                        .getAffectedAreasByDisaster(
                                disaster
                        );


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


        // Find existing flood event

        Disaster disaster =
                disasterService.getDisasterById(id);


        // Send it to edit page

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


        // Find original flood event

        Disaster existingDisaster =
                disasterService.getDisasterById(id);


        // =====================================================
        // PRESERVE ORIGINAL EVENT DATE
        // =====================================================

        /*
         * If the edit form does not submit the date,
         * keep the original date from the database.
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


    // =========================================================
    // DELETE FLOOD EVENT
    // =========================================================

    @PostMapping("/delete/{id}")
    public String deleteDisaster(

            @PathVariable Long id) {


        /*
         * Delete the selected flood event.
         *
         * The service first checks that the disaster exists.
         * The same disaster ID is then deleted.
         */

        disasterService.deleteDisaster(id);


        /*
         * Redirect back to the disaster list with a success
         * message.
         */

        return "redirect:/disasters?deleted=true";
    }
}