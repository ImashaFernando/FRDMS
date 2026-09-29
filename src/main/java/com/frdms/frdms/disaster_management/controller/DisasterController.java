package com.frdms.frdms.disaster_management.controller;

import com.frdms.frdms.disaster_management.entity.Disaster;
import com.frdms.frdms.disaster_management.service.AffectedAreaService;
import com.frdms.frdms.disaster_management.service.DisasterService;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.repository.VictimRepository;
import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/disasters")
public class DisasterController {


    private final DisasterService disasterService;

    private final AffectedAreaService affectedAreaService;

    private final AffectedFamilyInformationService
            familyInformationService;

    /*
     * VictimRepository is used to count how many
     * victims/families are registered for each
     * particular flood event.
     */
    private final VictimRepository victimRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DisasterController(
            DisasterService disasterService,
            AffectedAreaService affectedAreaService,
            AffectedFamilyInformationService familyInformationService,
            VictimRepository victimRepository) {

        this.disasterService =
                disasterService;

        this.affectedAreaService =
                affectedAreaService;

        this.familyInformationService =
                familyInformationService;

        this.victimRepository =
                victimRepository;
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
    // CREATE FLOOD EVENT
    // =========================================================

    @PostMapping
    public String createDisaster(

            @Valid
            @ModelAttribute("disaster")
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
    // VIEW ALL FLOOD EVENTS
    // =========================================================
    //
    // DM-06
    //
    // Display the number of victims/families
    // registered for EACH flood event.
    //
    // The count is obtained directly from:
    //
    // victims.disaster_id
    //
    // This means the number shown on the PM
    // disaster dashboard always matches the
    // disaster selected during victim registration.
    // =========================================================

    @GetMapping
    public String listDisasters(

            @RequestParam(
                    value = "keyword",
                    required = false
            )
            String keyword,

            Model model) {


        // -----------------------------------------------------
        // GET FLOOD EVENTS
        // -----------------------------------------------------

        List<Disaster> disasters =
                disasterService.searchDisasters(
                        keyword
                );


        // -----------------------------------------------------
        // FAMILY COUNT MAP
        // -----------------------------------------------------
        //
        // Key:
        //     Disaster ID
        //
        // Value:
        //     Number of registered victims/families
        //
        // Example:
        //
        // Disaster 4 -> 2 families
        // Disaster 5 -> 0 families
        // Disaster 6 -> 0 families
        // Disaster 7 -> 0 families
        // -----------------------------------------------------

        Map<Long, Long> affectedFamilyCounts =
                new HashMap<>();


        for (Disaster disaster : disasters) {


            Long disasterId =
                    disaster.getId();


            /*
             * Count victims registered for this
             * particular disaster.
             *
             * This reads:
             *
             * victims.disaster_id = disasterId
             */
            long familyCount =
                    victimRepository
                            .countByDisaster_Id(
                                    disasterId
                            );


            affectedFamilyCounts.put(
                    disasterId,
                    familyCount
            );
        }


        // -----------------------------------------------------
        // SEND DATA TO THYMELEAF
        // -----------------------------------------------------

        model.addAttribute(
                "disasters",
                disasters
        );


        model.addAttribute(
                "keyword",
                keyword
        );


        model.addAttribute(
                "affectedFamilyCounts",
                affectedFamilyCounts
        );


        return "disaster/list";
    }


    // =========================================================
    // VIEW FLOOD EVENT DETAILS
    // =========================================================
    //
    // DM-02 / DM-03 / DM-04 / DM-06
    //
    // Shows:
    // - Disaster information
    // - Affected areas
    // - Number of registered families
    // - Family information where available
    // =========================================================

    @GetMapping("/details/{id}")
    public String showDisasterDetails(

            @PathVariable Long id,

            Model model) {


        // -----------------------------------------------------
        // GET DISASTER
        // -----------------------------------------------------

        Disaster disaster =
                disasterService.getDisasterById(
                        id
                );


        // -----------------------------------------------------
        // GET AFFECTED AREAS
        // -----------------------------------------------------

        var affectedAreas =
                affectedAreaService
                        .getAffectedAreasByDisaster(
                                disaster
                        );


        // -----------------------------------------------------
        // GET REGISTERED FAMILY INFORMATION
        // -----------------------------------------------------
        //
        // We keep this part for the existing
        // affected-family information functionality.
        //
        // If family information records exist,
        // they can still be displayed.
        // -----------------------------------------------------

        List<AffectedFamilyInformation>
                affectedFamilies =
                familyInformationService
                        .getByDisaster(id);


        // -----------------------------------------------------
        // COUNT REGISTERED FAMILIES
        // -----------------------------------------------------
        //
        // IMPORTANT:
        //
        // The official registration count comes
        // directly from the victims table.
        //
        // This guarantees that the number matches
        // the disaster selected during registration.
        // -----------------------------------------------------

        long affectedFamilyCount =
                victimRepository
                        .countByDisaster_Id(id);


        // -----------------------------------------------------
        // COUNT FAMILIES REQUIRING SHELTER
        // -----------------------------------------------------

        long familiesRequiringShelter =
                familyInformationService
                        .countShelterRequiredFamilies(
                                id
                        );


        // -----------------------------------------------------
        // TOTAL FAMILY MEMBERS
        // -----------------------------------------------------

        long totalFamilyMembers =
                affectedFamilies
                        .stream()
                        .mapToLong(
                                family ->
                                        family
                                                .getTotalFamilyMembers()
                        )
                        .sum();


        // -----------------------------------------------------
        // SEND DATA TO THYMELEAF
        // -----------------------------------------------------

        model.addAttribute(
                "disaster",
                disaster
        );


        model.addAttribute(
                "affectedAreas",
                affectedAreas
        );


        model.addAttribute(
                "affectedFamilies",
                affectedFamilies
        );


        /*
         * This value is used by the
         * disaster details page.
         */
        model.addAttribute(
                "affectedFamilyCount",
                affectedFamilyCount
        );


        model.addAttribute(
                "familiesRequiringShelter",
                familiesRequiringShelter
        );


        model.addAttribute(
                "totalFamilyMembers",
                totalFamilyMembers
        );


        return "disaster/details";
    }


    // =========================================================
    // EDIT FLOOD EVENT
    // =========================================================

    @GetMapping("/edit/{id}")
    public String showEditForm(

            @PathVariable Long id,

            Model model) {


        // Find existing flood event
        Disaster disaster =
                disasterService.getDisasterById(
                        id
                );


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

            @Valid
            @ModelAttribute("disaster")
            Disaster disaster,

            BindingResult bindingResult) {


        // -----------------------------------------------------
        // GET EXISTING DISASTER
        // -----------------------------------------------------

        Disaster existingDisaster =
                disasterService.getDisasterById(
                        id
                );


        // -----------------------------------------------------
        // PRESERVE ORIGINAL EVENT DATE
        // -----------------------------------------------------

        /*
         * If the edit form does not submit a date,
         * keep the original date.
         */
        if (disaster.getEventDate() == null) {

            disaster.setEventDate(
                    existingDisaster.getEventDate()
            );
        }


        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        if (bindingResult.hasErrors()) {

            return "disaster/edit";
        }


        // -----------------------------------------------------
        // UPDATE DISASTER
        // -----------------------------------------------------

        disasterService.updateDisaster(
                id,
                disaster
        );


        // -----------------------------------------------------
        // RETURN TO DISASTER LIST
        // -----------------------------------------------------

        return "redirect:/disasters";
    }


    // =========================================================
    // DELETE FLOOD EVENT
    // =========================================================

    @PostMapping("/delete/{id}")
    public String deleteDisaster(

            @PathVariable Long id) {


        disasterService.deleteDisaster(
                id
        );


        return "redirect:/disasters?deleted=true";
    }

}