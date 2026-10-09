
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

        this.disasterService = disasterService;
        this.affectedAreaService = affectedAreaService;
        this.familyInformationService = familyInformationService;
        this.victimRepository = victimRepository;
    }

    // =========================================================
    // DM-01
    // CREATE FLOOD EVENT FORM
    // =========================================================

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        model.addAttribute("disaster", new Disaster());

        return "disaster/form";
    }

    // =========================================================
    // DM-01 / DM-02
    // CREATE FLOOD EVENT
    // =========================================================

    @PostMapping
    public String createDisaster(
            @Valid
            @ModelAttribute("disaster") Disaster disaster,
            BindingResult bindingResult) {

        // Check validation errors
        if (bindingResult.hasErrors()) {
            return "disaster/form";
        }

        // Save flood event
        disasterService.createDisaster(disaster);

        // Return to flood event list
        return "redirect:/disasters";
    }

    // =========================================================
    // VIEW ALL FLOOD EVENTS
    // =========================================================

    /*
     * DM-06
     *
     * Display the number of victims/families registered
     * for each flood event.
     *
     * The count is obtained from victims.disaster_id.
     */
    @GetMapping
    public String listDisasters(
            @RequestParam(value = "keyword", required = false)
            String keyword,
            Model model) {

        // Get active flood events
        List<Disaster> disasters =
                disasterService.searchDisasters(keyword);

        // Family count map
        Map<Long, Long> affectedFamilyCounts = new HashMap<>();

        for (Disaster disaster : disasters) {

            Long disasterId = disaster.getId();

            // Count victims registered for this disaster
            long familyCount =
                    victimRepository.countByDisaster_Id(disasterId);

            affectedFamilyCounts.put(disasterId, familyCount);
        }

        // Send data to Thymeleaf
        model.addAttribute("disasters", disasters);
        model.addAttribute("keyword", keyword);
        model.addAttribute("affectedFamilyCounts", affectedFamilyCounts);

        return "disaster/list";
    }

    // =========================================================
    // RECYCLE BIN - VIEW ARCHIVED FLOOD EVENTS
    // =========================================================

    @GetMapping("/recycle-bin")
    public String showRecycleBin(Model model) {

        // Get archived flood events
        List<Disaster> archivedDisasters =
                disasterService.getArchivedDisasters();

        // Send archived events to the Recycle Bin page
        model.addAttribute("disasters", archivedDisasters);

        return "disaster/recycle-bin";
    }

    // =========================================================
    // RECYCLE BIN - RESTORE FLOOD EVENT
    // =========================================================

    @PostMapping("/restore/{id}")
    public String restoreDisaster(@PathVariable Long id) {

        // Restore the archived flood event
        disasterService.restoreDisaster(id);

        // Return to the Recycle Bin page
        return "redirect:/disasters/recycle-bin?restored=true";
    }

    // =========================================================
    // VIEW FLOOD EVENT DETAILS
    // =========================================================

    /*
     * DM-02 / DM-03 / DM-04 / DM-06
     *
     * Shows:
     * - Disaster information
     * - Affected areas
     * - Number of registered families
     * - Family information where available
     */
    @GetMapping("/details/{id}")
    public String showDisasterDetails(
            @PathVariable Long id,
            Model model) {

        // Get disaster
        Disaster disaster = disasterService.getDisasterById(id);

        // Get affected areas
        var affectedAreas =
                affectedAreaService.getAffectedAreasByDisaster(disaster);

        // Get registered family information
        List<AffectedFamilyInformation> affectedFamilies =
                familyInformationService.getByDisaster(id);

        // Count registered families
        long affectedFamilyCount =
                victimRepository.countByDisaster_Id(id);

        // Count families requiring shelter
        long familiesRequiringShelter =
                familyInformationService.countShelterRequiredFamilies(id);

        // Calculate total family members
        long totalFamilyMembers =
                affectedFamilies.stream()
                        .mapToLong(
                                family -> family.getTotalFamilyMembers()
                        )
                        .sum();

        // Send data to Thymeleaf
        model.addAttribute("disaster", disaster);
        model.addAttribute("affectedAreas", affectedAreas);
        model.addAttribute("affectedFamilies", affectedFamilies);
        model.addAttribute("affectedFamilyCount", affectedFamilyCount);
        model.addAttribute(
                "familiesRequiringShelter",
                familiesRequiringShelter
        );
        model.addAttribute("totalFamilyMembers", totalFamilyMembers);

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
        Disaster disaster = disasterService.getDisasterById(id);

        model.addAttribute("disaster", disaster);

        return "disaster/edit";
    }

    // =========================================================
    // UPDATE FLOOD EVENT
    // =========================================================

    @PostMapping("/update/{id}")
    public String updateDisaster(
            @PathVariable Long id,
            @Valid
            @ModelAttribute("disaster") Disaster disaster,
            BindingResult bindingResult) {

        // Get existing disaster
        Disaster existingDisaster =
                disasterService.getDisasterById(id);

        // Preserve original event date if no date is submitted
        if (disaster.getEventDate() == null) {
            disaster.setEventDate(existingDisaster.getEventDate());
        }

        // Validation
        if (bindingResult.hasErrors()) {
            return "disaster/edit";
        }

        // Update disaster
        disasterService.updateDisaster(id, disaster);

        // Return to disaster list
        return "redirect:/disasters";
    }

    // =========================================================
    // ARCHIVE FLOOD EVENT
    // =========================================================

    @PostMapping("/delete/{id}")
    public String deleteDisaster(@PathVariable Long id) {

        /*
         * The existing delete service now archives the event
         * instead of permanently deleting it.
         */
        disasterService.deleteDisaster(id);

        return "redirect:/disasters?deleted=true";
    }
}
