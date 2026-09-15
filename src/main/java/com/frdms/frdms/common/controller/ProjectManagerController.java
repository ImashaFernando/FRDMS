package com.frdms.frdms.common.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectManagerController {

    // =========================
    // PROJECT MANAGER LOGIN
    // =========================

    @GetMapping("/project-manager/login")
    public String showProjectManagerLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {

        if (error != null) {
            model.addAttribute(
                    "loginError",
                    "Invalid email or password."
            );
        }

        if (logout != null) {
            model.addAttribute(
                    "logoutMessage",
                    "You have been logged out successfully."
            );
        }

        return "project-manager/project-manager-login";
    }


    // =========================
    // PROJECT MANAGER DASHBOARD
    // =========================

    @GetMapping("/project-manager/dashboard")
    public String showProjectManagerDashboard(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "projectManagerEmail",
                authentication.getName()
        );

        return "project-manager/project-manager-dashboard";
    }
}