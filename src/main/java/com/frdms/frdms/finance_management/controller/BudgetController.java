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


    // =========================================================
    // FINANCE OFFICER - CREATE BUDGET REQUEST
    // =========================================================

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


    // =========================================================
    // FINANCE OFFICER - VIEW ALL BUDGET RECORDS
    // =========================================================

    @GetMapping
    public String viewBudgets(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getAllBudgets()
        );

        return "finance/budget-records";
    }


    // =========================================================
    // FINANCE OFFICER - VIEW APPROVED BUDGETS
    // Used for allocation after PM approval
    // =========================================================

    @GetMapping("/approved")
    public String viewApprovedBudgets(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getApprovedBudgets()
        );

        return "finance/allocate-budget";
    }


    // =========================================================
    // FINANCE OFFICER - ALLOCATE APPROVED BUDGET
    // =========================================================

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


    @PostMapping("/allocate/{id}")
    public String allocateBudget(
            @PathVariable Long id,
            @RequestParam("allocatedAmount") Double allocatedAmount,
            Model model) {

        try {

            budgetService.allocateBudget(
                    id,
                    allocatedAmount
            );

            return "redirect:/finance/budgets/status";

        } catch (IllegalArgumentException | IllegalStateException e) {

            Budget budget =
                    budgetService.getBudgetById(id);

            model.addAttribute(
                    "budget",
                    budget
            );

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "finance/allocate-budget-form";
        }
    }


    // =========================================================
    // FINANCE OFFICER - BUDGET STATUS
    // =========================================================

    @GetMapping("/status")
    public String viewBudgetStatus(Model model) {

        List<Budget> budgets =
                budgetService.getAllBudgets();

        long pendingCount =
                budgets.stream()
                        .filter(b -> "PENDING".equals(b.getStatus()))
                        .count();

        long approvedCount =
                budgets.stream()
                        .filter(b -> "APPROVED".equals(b.getStatus()))
                        .count();

        long allocatedCount =
                budgets.stream()
                        .filter(b -> "ALLOCATED".equals(b.getStatus()))
                        .count();

        long rejectedCount =
                budgets.stream()
                        .filter(b -> "REJECTED".equals(b.getStatus()))
                        .count();


        model.addAttribute("budgets", budgets);

        model.addAttribute("pendingCount", pendingCount);

        model.addAttribute("approvedCount", approvedCount);

        model.addAttribute("allocatedCount", allocatedCount);

        model.addAttribute("rejectedCount", rejectedCount);


        return "finance/budget-status";
    }


    // =========================================================
    // FINANCE OFFICER - REJECTED BUDGET RECORDS
    // =========================================================

    @GetMapping("/trash")
    public String viewRejectedBudgets(Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getRejectedBudgets()
        );

        return "finance/budget-trash";
    }


    // =========================================================
    // BUDGET SUCCESS
    // =========================================================

    @GetMapping("/success")
    public String budgetSuccess() {

        return "finance/budget-success";
    }
}