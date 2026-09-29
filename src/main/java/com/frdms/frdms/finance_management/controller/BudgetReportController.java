package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.service.BudgetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BudgetReportController {

    private final BudgetService budgetService;

    public BudgetReportController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // =========================
    // F13 - Generate Budget Report
    // =========================

    @GetMapping("/finance/reports/budgets")
    public String generateBudgetReport(Model model) {

        List<Budget> budgets =
                budgetService.getBudgetReport();

        model.addAttribute(
                "budgets",
                budgets
        );

        // Total requirements
        model.addAttribute(
                "totalRequirements",
                budgets.size()
        );

        // Pending
        long pendingCount = budgets.stream()
                .filter(budget ->
                        "PENDING".equals(budget.getStatus()))
                .count();

        model.addAttribute(
                "pendingCount",
                pendingCount
        );

        // Approved
        long approvedCount = budgets.stream()
                .filter(budget ->
                        "APPROVED".equals(budget.getStatus()))
                .count();

        model.addAttribute(
                "approvedCount",
                approvedCount
        );

        // Allocated
        long allocatedCount = budgets.stream()
                .filter(budget ->
                        "ALLOCATED".equals(budget.getStatus()))
                .count();

        model.addAttribute(
                "allocatedCount",
                allocatedCount
        );

        // Total requested amount
        double totalRequestedAmount = budgets.stream()
                .mapToDouble(budget ->
                        budget.getRequestedAmount() != null
                                ? budget.getRequestedAmount()
                                : 0.0)
                .sum();

        model.addAttribute(
                "totalRequestedAmount",
                totalRequestedAmount
        );

        // Total allocated amount
        double totalAllocatedAmount = budgets.stream()
                .mapToDouble(budget ->
                        budget.getAllocatedAmount() != null
                                ? budget.getAllocatedAmount()
                                : 0.0)
                .sum();

        model.addAttribute(
                "totalAllocatedAmount",
                totalAllocatedAmount
        );

        return "finance/budget-report";
    }
}