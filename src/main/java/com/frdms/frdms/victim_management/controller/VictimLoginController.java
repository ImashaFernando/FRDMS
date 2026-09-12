package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.VictimService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class VictimLoginController {

    private final VictimService victimService;

    public VictimLoginController(VictimService victimService) {
        this.victimService = victimService;
    }


    // =========================================================
    // SHOW VICTIM LOGIN PAGE
    // =========================================================

    @GetMapping("/victim-login")
    public String showLoginPage() {

        return "victim/login";
    }


    // =========================================================
    // PROCESS VICTIM LOGIN
    // =========================================================

    @PostMapping("/victim-login")
    public String loginVictim(

            @RequestParam("nicNumber")
            String nicNumber,

            @RequestParam("password")
            String password,

            HttpSession session,

            Model model) {


        // -----------------------------------------------------
        // Find victim using NIC
        // -----------------------------------------------------

        Victim victim =
                victimService.getVictimByNic(nicNumber);


        // -----------------------------------------------------
        // Check victim and password
        // -----------------------------------------------------

        if (victim == null ||
                !victimService.checkPassword(
                        password,
                        victim.getPassword()
                )) {

            model.addAttribute(
                    "loginError",
                    "Invalid NIC number or password."
            );

            return "victim/login";
        }


        // -----------------------------------------------------
        // Store logged-in victim in session
        // -----------------------------------------------------

        session.setAttribute(
                "loggedInVictimId",
                victim.getId()
        );


        // -----------------------------------------------------
        // Redirect to victim dashboard
        // -----------------------------------------------------

        return "redirect:/victim/dashboard";
    }


    // =========================================================
    // VICTIM LOGOUT
    // =========================================================

    @GetMapping("/victim-logout")
    public String logoutVictim(HttpSession session) {

        session.invalidate();

        return "redirect:/victim-login";
    }
}