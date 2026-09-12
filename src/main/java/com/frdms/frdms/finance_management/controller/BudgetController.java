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


    // =========================
    // F07 - CREATE BUDGET
    // =========================

    @GetMapping("/create")
    public String showCreateBudgetForm(Model model) {

        model.addAttribute("budget", new Budget());

        return "finance/create-budget";
    }


    @PostMapping("/create")
    public String createBudgetRequirement(
            @Valid @ModelAttribute("budget") Budget budget,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return "finance/create-budget";
        }

        budgetService.createBudgetRequirement(budget);

        return "redirect:/finance/budgets/success";
    }


    // =========================
    // VIEW ALL
    // =========================

    @GetMapping
    public String viewBudgets(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getAllBudgets()
        );

        return "finance/budget-records";
    }


    // =========================
    // PM - PENDING BUDGETS
    // =========================

    @GetMapping("/pending")
    public String viewPendingBudgets(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getPendingBudgets()
        );

        return "finance/pm-budget-requirements";
    }


    // =========================
    // PM - APPROVE
    // =========================

    @PostMapping("/approve/{id}")
    public String approveBudget(
            @PathVariable Long id) {

        budgetService.approveBudget(id);

        return "redirect:/finance/budgets/pending";
    }


    // =========================
    // PM - REJECT
    // =========================

    @PostMapping("/reject/{id}")
    public String rejectBudget(
            @PathVariable Long id) {

        budgetService.rejectBudget(id);

        return "redirect:/finance/budgets/pending";
    }


    // =========================
    // REJECTED / TRASH
    // =========================

    @GetMapping("/trash")
    public String viewRejectedBudgets(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getRejectedBudgets()
        );

        return "finance/budget-trash";
    }


    // =========================
    // SUCCESS
    // =========================

    @GetMapping("/success")
    public String budgetSuccess() {

        return "finance/budget-success";
    }
}