package com.frdms.frdms.common.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectManagerController {


    // ================================
    // PROJECT MANAGER MAIN DASHBOARD
    // ================================

    @GetMapping("/project-manager/dashboard")
    public String showProjectManagerDashboard(
            Authentication authentication,
            Model model) {

        if (authentication != null) {
            model.addAttribute(
                    "projectManagerEmail",
                    authentication.getName()
            );
        }

        return "project-manager/project-manager-dashboard";
    }

}