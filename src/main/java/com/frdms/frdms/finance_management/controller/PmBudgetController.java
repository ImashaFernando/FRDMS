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

    // ==========================================
    // PM - VIEW PENDING BUDGET REQUESTS
    // ==========================================

    @GetMapping("/pending")
    public String viewPendingBudgets(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getPendingBudgets()
        );

        return "finance/pm-budget-requirements";
    }


    // ==========================================
    // PM - APPROVE BUDGET
    // ==========================================

    @PostMapping("/approve/{id}")
    public String approveBudget(@PathVariable Long id) {

        budgetService.approveBudget(id);

        return "redirect:/pm/budgets/pending";
    }


    // ==========================================
    // PM - REJECT BUDGET
    // ==========================================

    @PostMapping("/reject/{id}")
    public String rejectBudget(@PathVariable Long id) {

        budgetService.rejectBudget(id);

        return "redirect:/pm/budgets/pending";
    }
}