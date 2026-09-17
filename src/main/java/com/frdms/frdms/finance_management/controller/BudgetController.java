package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
@RequestMapping("/finance/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // =========================
    // F07 - Create Budget Requirement
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
    // View All Budget Records
    // =========================

    @GetMapping
    public String viewBudgets(Model model) {
        model.addAttribute("budgets", budgetService.getAllBudgets());
        return "finance/budget-records";
    }

    // =========================
    // PM - View Pending Budgets
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
    // PM - Approve Budget
    // =========================

    @PostMapping("/approve/{id}")
    public String approveBudget(@PathVariable Long id) {

        budgetService.approveBudget(id);

        return "redirect:/finance/budgets/pending";
    }

    // =========================
    // PM - Reject Budget
    // =========================

    @PostMapping("/reject/{id}")
    public String rejectBudget(@PathVariable Long id) {

        budgetService.rejectBudget(id);

        return "redirect:/finance/budgets/pending";
    }

    // =========================
    // Rejected / Trash
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
    // F08 - View Approved Budgets
    // =========================

    @GetMapping("/approved")
    public String viewApprovedBudgets(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getApprovedBudgets()
        );

        return "finance/allocate-budget";
    }

    // =========================
    // F08 - Show Allocation Form
    // =========================

    @GetMapping("/allocate/{id}")
    public String showAllocationForm(
            @PathVariable Long id,
            Model model) {

        Budget budget = budgetService.getBudgetById(id);

        if (!"APPROVED".equals(budget.getStatus())) {
            return "redirect:/finance/budgets/approved";
        }

        model.addAttribute("budget", budget);

        return "finance/allocate-budget-form";
    }

    // =========================
    // F08 - Allocate Budget
    // =========================

    @PostMapping("/allocate/{id}")
    public String allocateBudget(
            @PathVariable Long id,
            @RequestParam("allocatedAmount") Double allocatedAmount,
            Model model) {

        try {

            budgetService.allocateBudget(id, allocatedAmount);

            return "redirect:/finance/budgets/status";

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            Budget budget = budgetService.getBudgetById(id);

            model.addAttribute("budget", budget);
            model.addAttribute("error", e.getMessage());

            return "finance/allocate-budget-form";
        }
    }

    // =========================
    // F10 - Budget Status
    // =========================

    @GetMapping("/status")
    public String viewBudgetStatus(Model model) {

        List<Budget> budgets = budgetService.getAllBudgets();

        long pendingCount = budgets.stream()
                .filter(b -> "PENDING".equals(b.getStatus()))
                .count();

        long approvedCount = budgets.stream()
                .filter(b -> "APPROVED".equals(b.getStatus()))
                .count();

        long allocatedCount = budgets.stream()
                .filter(b -> "ALLOCATED".equals(b.getStatus()))
                .count();

        long rejectedCount = budgets.stream()
                .filter(b -> "REJECTED".equals(b.getStatus()))
                .count();

        model.addAttribute("budgets", budgets);

        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("allocatedCount", allocatedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "finance/budget-status";
    }

    // =========================
    // Success Page
    // =========================

    @GetMapping("/success")
    public String budgetSuccess() {
        return "finance/budget-success";
    }
}