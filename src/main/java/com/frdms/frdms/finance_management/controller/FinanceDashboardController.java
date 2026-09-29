package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.service.FinanceDashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FinanceDashboardController {

    private final FinanceDashboardService financeDashboardService;

    public FinanceDashboardController(
            FinanceDashboardService financeDashboardService
    ) {
        this.financeDashboardService = financeDashboardService;
    }

    @GetMapping("/finance/dashboard")
    public String financeDashboard(Model model) {

        model.addAttribute(
                "totalDonations",
                financeDashboardService.getTotalDonations()
        );

        model.addAttribute(
                "totalExpenses",
                financeDashboardService.getTotalExpenses()
        );

        model.addAttribute(
                "availableBudget",
                financeDashboardService.getAvailableBudget()
        );

        model.addAttribute(
                "pendingRequests",
                financeDashboardService.getPendingRequests()
        );

        model.addAttribute(
                "recentExpenses",
                financeDashboardService.getRecentExpenses()
        );

        model.addAttribute(
                "recentDonations",
                financeDashboardService.getRecentDonations()
        );

        return "finance/finance-dashboard";
    }
}