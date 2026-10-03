package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.disaster_management.entity.Disaster;
import com.frdms.frdms.disaster_management.service.DisasterService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/victims")
public class VictimController {

    private final VictimService victimService;
    private final GNReportService gnReportService;
    private final AffectedFamilyInformationService informationService;
    private final DisasterService disasterService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimController(
            VictimService victimService,
            GNReportService gnReportService,
            AffectedFamilyInformationService informationService,
            DisasterService disasterService) {

        this.victimService = victimService;
        this.gnReportService = gnReportService;
        this.informationService = informationService;
        this.disasterService = disasterService;
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

        // Load available flood events
        model.addAttribute(
                "disasters",
                disasterService.getAllDisasters()
        );

        return "victim/form";
    }


    // =========================================================
    // VM-01
    // REGISTER NEW VICTIM
    // =========================================================

    @PostMapping("/register")
    public String registerVictim(

            @Valid
            @ModelAttribute("victim")
            Victim victim,

            BindingResult bindingResult,

            @RequestParam(
                    value = "disasterId",
                    required = false
            )
            Long disasterId,

            Model model) {

        System.out.println("🔥 VICTIM REGISTER POST REACHED");


        // -----------------------------------------------------
        // ALWAYS RELOAD DISASTER LIST
        // -----------------------------------------------------

        model.addAttribute(
                "disasters",
                disasterService.getAllDisasters()
        );


        // -----------------------------------------------------
        // CHECK DISASTER SELECTION
        // -----------------------------------------------------

        if (disasterId == null) {

            model.addAttribute(
                    "disasterError",
                    "Please select the flood event affected your family."
            );

            return "victim/form";
        }


        // -----------------------------------------------------
        // FIND SELECTED DISASTER
        // -----------------------------------------------------

        Disaster selectedDisaster;

        try {

            selectedDisaster =
                    disasterService.getDisasterById(
                            disasterId
                    );

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "disasterError",
                    "The selected flood event could not be found."
            );

            return "victim/form";
        }


        // -----------------------------------------------------
        // CHECK VALIDATION ERRORS
        // -----------------------------------------------------

        if (bindingResult.hasErrors()) {
            return "victim/form";
        }


        // -----------------------------------------------------
        // CHECK DUPLICATE NIC
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
        // CONNECT VICTIM WITH DISASTER
        // -----------------------------------------------------

        victim.setDisaster(
                selectedDisaster
        );


        // -----------------------------------------------------
        // SAVE VICTIM
        // -----------------------------------------------------

        victimService.registerVictim(
                victim
        );


        // -----------------------------------------------------
        // REDIRECT TO LOGIN
        // -----------------------------------------------------

        return "redirect:/login";
    }


    // =========================================================
    // DISPLAY VICTIMS
    // ADMIN / PROJECT MANAGER SIDE
    //
    // FILTERS:
    // 1. NIC Number
    // 2. Shelter Requirement
    // =========================================================

    @GetMapping
    public String listVictims(

            @RequestParam(
                    value = "nic",
                    required = false
            )
            String nic,

            @RequestParam(
                    value = "shelter",
                    required = false
            )
            String shelter,

            Model model) {


        // =====================================================
        // STEP 1
        // GET ALL VICTIMS
        // =====================================================

        List<Victim> allVictims =
                victimService.getAllVictims();


        // =====================================================
        // STEP 2
        // CREATE FAMILY INFORMATION MAP
        //
        // Key   = Victim ID
        // Value = AffectedFamilyInformation
        // =====================================================

        Map<Long, AffectedFamilyInformation>
                familyInformationMap =
                new HashMap<>();


        for (Victim victim : allVictims) {

            informationService
                    .getByVictim(victim)
                    .ifPresent(
                            information ->
                                    familyInformationMap.put(
                                            victim.getId(),
                                            information
                                    )
                    );
        }


        // =====================================================
        // STEP 3
        // NORMALIZE FILTER VALUES
        // =====================================================

        String cleanNic = "";

        if (nic != null) {
            cleanNic = nic.trim();
        }


        String cleanShelter = "";

        if (shelter != null) {
            cleanShelter =
                    shelter.trim().toLowerCase();
        }


        // =====================================================
        // STEP 4
        // FILTER VICTIMS
        // =====================================================

        List<Victim> filteredVictims =
                new ArrayList<>();


        for (Victim victim : allVictims) {


            // -------------------------------------------------
            // NIC FILTER
            // -------------------------------------------------

            boolean nicMatches = true;

            if (!cleanNic.isEmpty()) {

                nicMatches =
                        victim.getNicNumber() != null
                                &&
                                victim.getNicNumber()
                                        .toLowerCase()
                                        .contains(
                                                cleanNic.toLowerCase()
                                        );
            }


            // -------------------------------------------------
            // SHELTER FILTER
            // -------------------------------------------------

            boolean shelterMatches = true;

            AffectedFamilyInformation information =
                    familyInformationMap.get(
                            victim.getId()
                    );


            if (!cleanShelter.isEmpty()
                    && !cleanShelter.equals("all")
                    && !cleanShelter.equals("all-victims")) {


                // =============================================
                // SHELTER REQUIRED
                // =============================================

                if (
                        cleanShelter.equals("required")
                                ||
                                cleanShelter.equals("shelter-required")
                                ||
                                cleanShelter.equals("true")
                ) {

                    shelterMatches =
                            information != null
                                    &&
                                    Boolean.TRUE.equals(
                                            information.getShelterRequired()
                                    );
                }


                // =============================================
                // SHELTER NOT REQUIRED
                // =============================================

                else if (
                        cleanShelter.equals("not-required")
                                ||
                                cleanShelter.equals("not_required")
                                ||
                                cleanShelter.equals("shelter-not-required")
                                ||
                                cleanShelter.equals("false")
                ) {

                    shelterMatches =
                            information != null
                                    &&
                                    Boolean.FALSE.equals(
                                            information.getShelterRequired()
                                    );
                }


                // =============================================
                // FAMILY INFORMATION NOT SUBMITTED
                // =============================================

                else if (
                        cleanShelter.equals("not-submitted")
                                ||
                                cleanShelter.equals("not_submitted")
                ) {

                    shelterMatches =
                            information == null;
                }
            }


            // -------------------------------------------------
            // ADD ONLY IF BOTH FILTERS MATCH
            // -------------------------------------------------

            if (nicMatches && shelterMatches) {

                filteredVictims.add(
                        victim
                );
            }
        }


        // =====================================================
        // STEP 5
        // SEND FILTERED VICTIMS TO THYMELEAF
        // =====================================================

        model.addAttribute(
                "victims",
                filteredVictims
        );


        // =====================================================
        // STEP 6
        // SEND FAMILY INFORMATION MAP
        // =====================================================

        model.addAttribute(
                "familyInformationMap",
                familyInformationMap
        );


        // =====================================================
        // STEP 7
        // KEEP FILTER VALUES IN THE PAGE
        // =====================================================

        model.addAttribute(
                "filterNic",
                cleanNic
        );

        model.addAttribute(
                "filterShelter",
                cleanShelter
        );


        // =====================================================
        // STEP 8
        // TOTAL RESULT COUNT
        // =====================================================

        model.addAttribute(
                "filteredCount",
                filteredVictims.size()
        );


        return "victim/list";
    }


    // =========================================================
    // VM-02
    // VIEW COMPLETE VICTIM INFORMATION
    // =========================================================

    @GetMapping("/{id}")
    public String viewVictim(

            @PathVariable Long id,

            Model model) {


        // -----------------------------------------------------
        // GET VICTIM
        // -----------------------------------------------------

        Victim victim =
                victimService.getVictimById(id);


        if (victim == null) {
            return "redirect:/victims";
        }


        model.addAttribute(
                "victim",
                victim
        );


        // -----------------------------------------------------
        // GET GN REPORTS
        // -----------------------------------------------------

        List<GNReport> victimGNReports =
                new ArrayList<>();


        for (
                GNReport report :
                gnReportService.getAllGNReports()
        ) {

            if (
                    report.getVictim() != null
                            &&
                            report.getVictim().getId() != null
                            &&
                            report.getVictim()
                                    .getId()
                                    .equals(victim.getId())
            ) {

                victimGNReports.add(
                        report
                );
            }
        }


        model.addAttribute(
                "gnReports",
                victimGNReports
        );


        // -----------------------------------------------------
        // GET FAMILY & AFFECTED INFORMATION
        // -----------------------------------------------------

        AffectedFamilyInformation familyInformation =
                informationService
                        .getByVictim(victim)
                        .orElse(null);


        model.addAttribute(
                "familyInformation",
                familyInformation
        );


        return "victim/view";
    }


    // =========================================================
    // VM-02
    // SHOW EDIT FORM
    // =========================================================

    @GetMapping("/edit/{id}")
    public String showEditForm(

            @PathVariable Long id,

            Model model) {


        Victim victim =
                victimService.getVictimById(id);


        if (victim == null) {
            return "redirect:/victims";
        }


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

            @Valid
            @ModelAttribute("victim")
            Victim victim,

            BindingResult bindingResult,

            Model model) {


        // -----------------------------------------------------
        // GET EXISTING VICTIM
        // -----------------------------------------------------

        Victim existingVictim =
                victimService.getVictimById(id);


        if (existingVictim == null) {
            return "redirect:/victims";
        }


        // -----------------------------------------------------
        // KEEP EXISTING PASSWORD
        // -----------------------------------------------------

        victim.setPassword(
                existingVictim.getPassword()
        );


        // -----------------------------------------------------
        // KEEP EXISTING NIC
        // -----------------------------------------------------

        victim.setNicNumber(
                existingVictim.getNicNumber()
        );


        // -----------------------------------------------------
        // KEEP EXISTING DISASTER
        // -----------------------------------------------------

        victim.setDisaster(
                existingVictim.getDisaster()
        );


        // -----------------------------------------------------
        // PRESERVE AFFECTED AREA
        //
        // The current Victim edit page does not yet edit
        // affected-area information.
        // Therefore, do not accidentally erase it.
        // -----------------------------------------------------

        victim.setAffectedArea(
                existingVictim.getAffectedArea()
        );


        // -----------------------------------------------------
        // PRESERVE SHELTER ASSIGNMENT
        //
        // The current Victim edit page does not yet edit
        // shelter assignment.
        // Therefore, do not accidentally erase it.
        // -----------------------------------------------------

        victim.setShelter(
                existingVictim.getShelter()
        );


        // -----------------------------------------------------
        // KEEP REGISTRATION DATE
        // -----------------------------------------------------

        victim.setRegistrationDate(
                existingVictim.getRegistrationDate()
        );


        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        if (bindingResult.hasErrors()) {
            return "victim/edit";
        }


        // -----------------------------------------------------
        // UPDATE VICTIM
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