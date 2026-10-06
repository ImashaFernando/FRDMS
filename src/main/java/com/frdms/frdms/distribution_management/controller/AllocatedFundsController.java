package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.finance_management.service.BudgetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/distribution")
public class AllocatedFundsController {

    private final BudgetService budgetService;

    public AllocatedFundsController(
            BudgetService budgetService) {

        this.budgetService = budgetService;
    }

    // =========================================================
    // DISTRIBUTION OFFICER - VIEW ALLOCATED FUNDS
    // =========================================================

    @GetMapping("/allocated-funds")
    public String viewAllocatedFunds(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getAllocatedBudgets()
        );

        return "distribution/allocated-funds";
    }
}