package com.frdms.frdms.distribution_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DistributionDashboardController {

    @GetMapping("/distribution/dashboard")
    public String showDashboard(Model model) {

        model.addAttribute("pageTitle", "Distribution Management");

        return "distribution/dashboard";
    }
}