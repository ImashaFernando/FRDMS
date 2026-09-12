package com.frdms.frdms.finance_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PmDashboardController {

    @GetMapping("/pm/dashboard")
    public String pmDashboard() {
        return "finance/pm-dashboard";
    }
}