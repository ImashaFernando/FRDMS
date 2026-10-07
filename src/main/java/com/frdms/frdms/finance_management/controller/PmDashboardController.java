package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.donation_management.service.DonationService;
import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.service.BudgetService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PmDashboardController {

    private final DonationService donationService;
    private final BudgetService budgetService;

    public PmDashboardController(
            DonationService donationService,
            BudgetService budgetService) {

        this.donationService = donationService;
        this.budgetService = budgetService;
    }


    // =====================================================
    // PROJECT MANAGER DASHBOARD
    // =====================================================

    @GetMapping("/pm/dashboard")
    public String pmDashboard(Model model) {


        // =================================================
        // DONATION INFORMATION
        // =================================================

        model.addAttribute(
                "totalDonations",
                donationService.getTotalCashDonationAmount()
        );

        model.addAttribute(
                "totalCashDonations",
                donationService.getTotalCashDonationAmount()
        );

        model.addAttribute(
                "totalGoodsDonations",
                donationService.getTotalGoodsDonationCount()
        );


        // =================================================
        // BUDGET INFORMATION
        // =================================================

        model.addAttribute(
                "availableBudget",
                budgetService.getAvailableBudget()
        );


        // Get pending budgets once
        List<Budget> pendingBudgets =
                budgetService.getPendingBudgets();


        model.addAttribute(
                "pendingRequirements",
                pendingBudgets.size()
        );


        model.addAttribute(
                "pendingBudgets",
                pendingBudgets
        );


        // =================================================
        // RETURN PM DASHBOARD
        // =================================================

        return "finance/pm-dashboard";
    }
}