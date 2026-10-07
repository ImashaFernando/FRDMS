package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.service.BudgetService;
import com.frdms.frdms.finance_management.service.FinanceDashboardService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FinanceDashboardController {

    private final FinanceDashboardService financeDashboardService;
    private final BudgetService budgetService;

    public FinanceDashboardController(
            FinanceDashboardService financeDashboardService,
            BudgetService budgetService
    ) {
        this.financeDashboardService =
                financeDashboardService;

        this.budgetService =
                budgetService;
    }

    @GetMapping("/finance/dashboard")
    public String financeDashboard(Model model) {

        model.addAttribute(
                "totalDonations",
                financeDashboardService.getTotalDonations()
        );

        model.addAttribute(
                "availableBudget",
                budgetService.getAvailableBudget()
        );

        model.addAttribute(
                "pendingRequests",
                financeDashboardService.getPendingRequests()
        );

        model.addAttribute(
                "recentDonations",
                financeDashboardService.getRecentDonations()
        );

        return "finance/finance-dashboard";
    }
}