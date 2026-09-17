package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.donation_management.service.DonationService;
import com.frdms.frdms.finance_management.service.BudgetService;
import com.frdms.frdms.finance_management.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PmDashboardController {

    private final DonationService donationService;
    private final ExpenseService expenseService;
    private final BudgetService budgetService;

    public PmDashboardController(
            DonationService donationService,
            ExpenseService expenseService,
            BudgetService budgetService) {

        this.donationService = donationService;
        this.expenseService = expenseService;
        this.budgetService = budgetService;
    }

    @GetMapping("/pm/dashboard")
    public String pmDashboard(Model model) {

        // Donation information
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


        // Finance information
        model.addAttribute(
                "totalExpenses",
                expenseService.getTotalExpenseAmount()
        );


        // Budget information
        model.addAttribute(
                "availableBudget",
                budgetService.getAvailableBudget()
        );

        model.addAttribute(
                "pendingRequirements",
                budgetService.getPendingBudgets().size()
        );

        model.addAttribute(
                "pendingBudgets",
                budgetService.getPendingBudgets()
        );

        model.addAttribute(
                "approvedBudgets",
                budgetService.getApprovedBudgets()
        );

        model.addAttribute(
                "rejectedBudgets",
                budgetService.getRejectedBudgets()
        );


        return "finance/pm-dashboard";
    }
}