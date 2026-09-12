package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.GNReport;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.GNReportService;
import com.frdms.frdms.victim_management.service.VictimService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/victims")
public class VictimController {

    private final VictimService victimService;
    private final GNReportService gnReportService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimController(
            VictimService victimService,
            GNReportService gnReportService) {

        this.victimService = victimService;
        this.gnReportService = gnReportService;
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
    // =========================================================

    @GetMapping
    public String listVictims(Model model) {

        model.addAttribute(
                "victims",
                victimService.getAllVictims()
        );

        return "victim/list";
    }


    // =========================================================
    // VM-02
    // VIEW VICTIM INFORMATION
    // ADMIN / PM SIDE
    //
    // Also loads GN reports submitted by this victim.
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
        // Add victim information to page
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