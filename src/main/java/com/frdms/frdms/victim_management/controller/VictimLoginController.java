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
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        // -----------------------------------------------------
        // Find victim using username
        // -----------------------------------------------------

        Victim victim =
                victimService.getVictimByUsername(
                        username.trim()
                );


        // -----------------------------------------------------
        // Victim does not exist
        // -----------------------------------------------------

        if (victim == null) {

            model.addAttribute(
                    "loginError",
                    "Invalid username or password."
            );

            return "victim/login";
        }


        // -----------------------------------------------------
        // Check password
        // -----------------------------------------------------

        boolean passwordCorrect =
                victimService.checkPassword(
                        password,
                        victim.getPassword()
                );


        // -----------------------------------------------------
        // Wrong password
        // -----------------------------------------------------

        if (!passwordCorrect) {

            model.addAttribute(
                    "loginError",
                    "Invalid username or password."
            );

            return "victim/login";
        }


        // -----------------------------------------------------
        // Login successful
        // -----------------------------------------------------

        session.setAttribute(
                "victimId",
                victim.getId()
        );


        // -----------------------------------------------------
        // Go to victim dashboard
        // -----------------------------------------------------

        return "redirect:/victim/dashboard";
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    @GetMapping("/victim-logout")
    public String logoutVictim(HttpSession session) {

        session.invalidate();

        return "redirect:/victim-login";
    }
}