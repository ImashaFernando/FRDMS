package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.VictimService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VictimDashboardController {

    private final VictimService victimService;

    public VictimDashboardController(VictimService victimService) {
        this.victimService = victimService;
    }


    // =========================================================
    // VICTIM DASHBOARD
    // =========================================================

    @GetMapping("/victim/dashboard")
    public String showDashboard(
            HttpSession session,
            Model model) {

        // -----------------------------------------------------
        // Get logged-in victim ID from session
        // -----------------------------------------------------

        Long victimId =
                (Long) session.getAttribute("loggedInVictimId");


        // -----------------------------------------------------
        // If no victim is logged in, go back to login
        // -----------------------------------------------------

        if (victimId == null) {

            return "redirect:/victim-login";
        }


        // -----------------------------------------------------
        // Get victim information from database
        // -----------------------------------------------------

        Victim victim =
                victimService.getVictimById(victimId);


        // -----------------------------------------------------
        // Send victim information to Thymeleaf
        // -----------------------------------------------------

        model.addAttribute(
                "victim",
                victim
        );


        return "victim/dashboard";
    }
}