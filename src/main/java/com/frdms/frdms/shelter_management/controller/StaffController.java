package com.frdms.frdms.shelter_management.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StaffController {

    @GetMapping("/staff/login")
    public String staffLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {

        if (error != null) {
            model.addAttribute(
                    "errorMessage",
                    "Invalid email or password."
            );
        }

        if (logout != null) {
            model.addAttribute(
                    "logoutMessage",
                    "You have been logged out successfully."
            );
        }

        return "login";
    }

    @GetMapping("/staff/dashboard")
    public String staffDashboard(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "staffEmail",
                authentication.getName()
        );

        return "shelter/staff-dashboard";
    }
}