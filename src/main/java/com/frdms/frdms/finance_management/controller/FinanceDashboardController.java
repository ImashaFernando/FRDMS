package com.frdms.frdms.finance_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FinanceDashboardController {

    @GetMapping("/finance/dashboard")
    public String financeDashboard() {
        return "finance/finance-dashboard";
    }
}