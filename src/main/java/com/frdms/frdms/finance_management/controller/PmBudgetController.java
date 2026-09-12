package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.service.BudgetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pm/budgets")
public class PmBudgetController {

    private final BudgetService budgetService;

    public PmBudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }


    // View pending budget requirements
    @GetMapping("/pending")
    public String viewPendingBudgets(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getPendingBudgets()
        );

        return "finance/pm-budget-requirements";
    }


    // View a single budget requirement
    @GetMapping("/review/{id}")
    public String reviewBudget(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "budget",
                budgetService.getBudgetById(id)
        );

        return "finance/pm-budget-review";
    }


    // Approve
    @PostMapping("/approve/{id}")
    public String approveBudget(
            @PathVariable Long id) {

        budgetService.approveBudget(id);

        return "redirect:/pm/budgets/pending?success=approved";
    }


    // Reject
    @PostMapping("/reject/{id}")
    public String rejectBudget(
            @PathVariable Long id) {

        budgetService.rejectBudget(id);

        return "redirect:/pm/budgets/pending?success=rejected";
    }

}