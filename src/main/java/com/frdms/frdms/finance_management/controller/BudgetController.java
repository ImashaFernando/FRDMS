package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/finance/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Show Create Budget Requirement page
    @GetMapping("/create")
    public String showCreateBudgetForm(Model model) {

        model.addAttribute("budget", new Budget());

        return "finance/create-budget";
    }

    // Submit Budget Requirement
    @PostMapping("/create")
    public String createBudgetRequirement(
            @Valid @ModelAttribute("budget") Budget budget,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "finance/create-budget";
        }

        budgetService.createBudgetRequirement(budget);

        return "redirect:/finance/budgets/success";
    }

    // Success page
    @GetMapping("/success")
    public String budgetSuccess() {
        return "finance/budget-success";
    }
}