package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.VictimService;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class VictimLoginController {


    private final VictimService victimService;

    private final PasswordEncoder passwordEncoder;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimLoginController(
            VictimService victimService,
            PasswordEncoder passwordEncoder) {

        this.victimService = victimService;

        this.passwordEncoder = passwordEncoder;
    }


    // =========================================================
    // SHOW VICTIM LOGIN PAGE
    // =========================================================

    @GetMapping("/victims/login")
    public String showLoginPage() {

        return "victim/login";
    }


    // =========================================================
    // PROCESS VICTIM LOGIN
    // =========================================================

    @PostMapping("/victims/login")
    public String loginVictim(

            @RequestParam("nicNumber")
            String nicNumber,

            @RequestParam("password")
            String password,

            HttpSession session,

            Model model) {


        // -----------------------------------------------------
        // CHECK NIC
        // -----------------------------------------------------

        if (nicNumber == null
                || nicNumber.trim().isEmpty()) {

            model.addAttribute(
                    "loginError",
                    "Please enter your NIC number."
            );

            return "victim/login";
        }


        // -----------------------------------------------------
        // CHECK PASSWORD
        // -----------------------------------------------------

        if (password == null
                || password.isEmpty()) {

            model.addAttribute(
                    "loginError",
                    "Please enter your password."
            );

            return "victim/login";
        }


        // -----------------------------------------------------
        // CLEAN NIC
        // -----------------------------------------------------

        String cleanNic =
                nicNumber.trim();


        // -----------------------------------------------------
        // FIND VICTIM USING NIC
        // -----------------------------------------------------

        Victim victim =
                victimService.getVictimByNic(cleanNic);


        // -----------------------------------------------------
        // VICTIM NOT FOUND
        // -----------------------------------------------------

        if (victim == null) {

            model.addAttribute(
                    "loginError",
                    "Invalid NIC number or password."
            );

            return "victim/login";
        }


        // -----------------------------------------------------
        // CHECK PASSWORD USING BCRYPT
        // -----------------------------------------------------

        String storedPassword =
                victim.getPassword();


        if (storedPassword == null
                || storedPassword.trim().isEmpty()
                || !passwordEncoder.matches(
                password,
                storedPassword
        )) {

            model.addAttribute(
                    "loginError",
                    "Invalid NIC number or password."
            );

            return "victim/login";
        }


        // =====================================================
        // LOGIN SUCCESSFUL
        // =====================================================


        // -----------------------------------------------------
        // STORE VICTIM ID
        // -----------------------------------------------------

        session.setAttribute(
                "victimId",
                victim.getId()
        );


        // -----------------------------------------------------
        // STORE VICTIM NIC
        // -----------------------------------------------------

        session.setAttribute(
                "victimNic",
                victim.getNicNumber()
        );


        // -----------------------------------------------------
        // STORE VICTIM NAME
        // -----------------------------------------------------

        session.setAttribute(
                "victimName",
                victim.getFullName()
        );


        // -----------------------------------------------------
        // STORE DISASTER ID
        // DM-06
        // -----------------------------------------------------

        if (victim.getDisaster() != null) {

            session.setAttribute(
                    "victimDisasterId",
                    victim.getDisaster().getId()
            );

        } else {

            // Remove old disaster ID if the victim
            // has no disaster assigned.

            session.removeAttribute(
                    "victimDisasterId"
            );
        }


        // -----------------------------------------------------
        // GO TO VICTIM DASHBOARD
        // -----------------------------------------------------

        return "redirect:/victim/dashboard";
    }


    // =========================================================
    // VICTIM LOGOUT
    // =========================================================

    @GetMapping("/victims/logout")
    public String logoutVictim(
            HttpSession session) {

        session.invalidate();

        return "redirect:/victims/login";
    }
}