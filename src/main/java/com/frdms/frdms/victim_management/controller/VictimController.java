package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.entity.GNReport;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;
import com.frdms.frdms.victim_management.service.GNReportService;
import com.frdms.frdms.victim_management.service.VictimService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/victims")
public class VictimController {


    private final VictimService victimService;

    private final GNReportService gnReportService;

    private final AffectedFamilyInformationService informationService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimController(

            VictimService victimService,

            GNReportService gnReportService,

            AffectedFamilyInformationService informationService) {

        this.victimService = victimService;

        this.gnReportService = gnReportService;

        this.informationService = informationService;
    }


    // =========================================================
    // VM-01
    // SHOW VICTIM REGISTRATION FORM
    // =========================================================

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute(
                "victim",
                new Victim()
        );

        return "victim/form";
    }


    // =========================================================
    // VM-01
    // REGISTER NEW VICTIM
    // =========================================================

    @PostMapping("/register")
    public String registerVictim(

            @Valid @ModelAttribute("victim")
            Victim victim,

            BindingResult bindingResult,

            Model model) {


        // -----------------------------------------------------
        // Check validation errors
        // -----------------------------------------------------

        if (bindingResult.hasErrors()) {

            return "victim/form";
        }


        // -----------------------------------------------------
        // Check duplicate NIC
        // -----------------------------------------------------

        Victim existingVictim =
                victimService.getVictimByNic(
                        victim.getNicNumber()
                );


        if (existingVictim != null) {

            model.addAttribute(
                    "nicError",
                    "A victim with this NIC number is already registered."
            );

            return "victim/form";
        }


        // -----------------------------------------------------
        // Check duplicate username
        // -----------------------------------------------------

        Victim existingUsername =
                victimService.getVictimByUsername(
                        victim.getUsername()
                );


        if (existingUsername != null) {

            model.addAttribute(
                    "usernameError",
                    "This username is already taken. Please choose another username."
            );

            return "victim/form";
        }


        // -----------------------------------------------------
        // Register victim
        //
        // VictimService hashes the password before
        // saving it to MySQL.
        // -----------------------------------------------------

        victimService.registerVictim(victim);


        // -----------------------------------------------------
        // Send victim to login page
        // -----------------------------------------------------

        return "redirect:/victim-login";
    }


    // =========================================================
    // DISPLAY ALL VICTIMS
    // ADMIN / PM SIDE
    //
    // Supports:
    // 1. Search by NIC number
    // 2. Filter by shelter requirement
    // 3. Combination of both filters
    //
    // Also loads Family & Affected Information
    // for each victim.
    // =========================================================

    @GetMapping
    public String listVictims(

            @RequestParam(required = false)
            String nic,

            @RequestParam(required = false)
            String shelter,

            Model model) {


        // -----------------------------------------------------
        // Get all registered victims
        // -----------------------------------------------------

        List<Victim> victims =
                victimService.getAllVictims();


        // -----------------------------------------------------
        // Create a map containing Family & Affected
        // Information for each victim.
        //
        // Key   = Victim ID
        // Value = AffectedFamilyInformation
        // -----------------------------------------------------

        Map<Long, AffectedFamilyInformation>
                familyInformationMap =
                new HashMap<>();


        // -----------------------------------------------------
        // Find Family & Affected Information
        // for each victim.
        // -----------------------------------------------------

        for (Victim victim : victims) {

            informationService
                    .getByVictim(victim)
                    .ifPresent(information ->

                            familyInformationMap.put(
                                    victim.getId(),
                                    information
                            )
                    );
        }


        // =====================================================
        // FILTER 1
        // SEARCH BY NIC NUMBER
        // =====================================================

        if (nic != null && !nic.trim().isEmpty()) {

            String searchNic =
                    nic.trim();

            victims = victims.stream()
                    .filter(victim ->
                            victim.getNicNumber() != null
                                    && victim.getNicNumber()
                                    .equalsIgnoreCase(searchNic)
                    )
                    .toList();
        }


        // =====================================================
        // FILTER 2
        // SHELTER REQUIREMENT
        // =====================================================

        if (shelter != null
                && !shelter.trim().isEmpty()) {


            // -------------------------------------------------
            // Show only victims who require shelter
            // -------------------------------------------------

            if (shelter.equalsIgnoreCase("required")) {

                victims = victims.stream()
                        .filter(victim -> {

                            AffectedFamilyInformation information =
                                    familyInformationMap
                                            .get(victim.getId());

                            return information != null
                                    && Boolean.TRUE.equals(
                                    information.getShelterRequired()
                            );
                        })
                        .toList();
            }


            // -------------------------------------------------
            // Show only victims who do NOT require shelter
            // -------------------------------------------------

            else if (shelter.equalsIgnoreCase("not-required")) {

                victims = victims.stream()
                        .filter(victim -> {

                            AffectedFamilyInformation information =
                                    familyInformationMap
                                            .get(victim.getId());

                            return information != null
                                    && Boolean.FALSE.equals(
                                    information.getShelterRequired()
                            );
                        })
                        .toList();
            }
        }


        // -----------------------------------------------------
        // Send filtered victims to Thymeleaf page
        // -----------------------------------------------------

        model.addAttribute(
                "victims",
                victims
        );


        // -----------------------------------------------------
        // Send Family & Affected Information map
        // to Thymeleaf page.
        // -----------------------------------------------------

        model.addAttribute(
                "familyInformationMap",
                familyInformationMap
        );


        // -----------------------------------------------------
        // Keep NIC filter value in the page
        // -----------------------------------------------------

        model.addAttribute(
                "nic",
                nic
        );


        // -----------------------------------------------------
        // Keep shelter filter value in the page
        // -----------------------------------------------------

        model.addAttribute(
                "shelter",
                shelter
        );


        return "victim/list";
    }


    // =========================================================
    // VM-02
    // VIEW COMPLETE VICTIM INFORMATION
    // ADMIN / PM SIDE
    //
    // Also loads:
    // 1. GN Reports
    // 2. Family & Affected Information
    // =========================================================

    @GetMapping("/{id}")
    public String viewVictim(

            @PathVariable Long id,

            Model model) {


        // -----------------------------------------------------
        // Get victim
        // -----------------------------------------------------

        Victim victim =
                victimService.getVictimById(id);


        // -----------------------------------------------------
        // Add victim information
        // -----------------------------------------------------

        model.addAttribute(
                "victim",
                victim
        );


        // -----------------------------------------------------
        // Get GN reports submitted by this victim
        // -----------------------------------------------------

        model.addAttribute(
                "gnReports",
                gnReportService.getGNReportsByVictim(victim)
        );


        // -----------------------------------------------------
        // Get Family & Affected Information
        // -----------------------------------------------------

        AffectedFamilyInformation familyInformation =
                informationService
                        .getByVictim(victim)
                        .orElse(null);


        // -----------------------------------------------------
        // Add Family & Affected Information
        //
        // If the victim has not submitted it yet,
        // the value will be null.
        // -----------------------------------------------------

        model.addAttribute(
                "familyInformation",
                familyInformation
        );


        return "victim/view";
    }


    // =========================================================
    // VM-02
    // SHOW EDIT / RECORD FORM
    // =========================================================

    @GetMapping("/edit/{id}")
    public String showEditForm(

            @PathVariable Long id,

            Model model) {


        Victim victim =
                victimService.getVictimById(id);


        model.addAttribute(
                "victim",
                victim
        );


        return "victim/edit";
    }


    // =========================================================
    // VM-02
    // UPDATE VICTIM INFORMATION
    // =========================================================

    @PostMapping("/update/{id}")
    public String updateVictim(

            @PathVariable Long id,

            @Valid @ModelAttribute("victim")
            Victim victim,

            BindingResult bindingResult,

            Model model) {


        // -----------------------------------------------------
        // Get existing victim
        // -----------------------------------------------------

        Victim existingVictim =
                victimService.getVictimById(id);


        // -----------------------------------------------------
        // Keep existing username
        //
        // Username is used for victim login.
        // We do not allow the edit form to accidentally
        // replace it with a blank value.
        // -----------------------------------------------------

        victim.setUsername(
                existingVictim.getUsername()
        );


        // -----------------------------------------------------
        // Keep existing password
        //
        // VM-02 edit does not change the victim password.
        // -----------------------------------------------------

        victim.setPassword(
                existingVictim.getPassword()
        );


        // -----------------------------------------------------
        // Check validation errors
        // -----------------------------------------------------

        if (bindingResult.hasErrors()) {

            return "victim/edit";
        }


        // -----------------------------------------------------
        // Update victim
        // -----------------------------------------------------

        victimService.updateVictim(
                id,
                victim
        );


        return "redirect:/victims";
    }


    // =========================================================
    // DELETE VICTIM
    // =========================================================

    @PostMapping("/delete/{id}")
    public String deleteVictim(

            @PathVariable Long id) {


        victimService.deleteVictim(id);


        return "redirect:/victims";
    }

}