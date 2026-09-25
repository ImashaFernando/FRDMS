package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.entity.Expense;
import com.frdms.frdms.finance_management.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ExpenseReportController {

    private final ExpenseService expenseService;

    public ExpenseReportController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // =========================
    // F12 - Generate Expense Report
    // =========================

    @GetMapping("/finance/reports/expenses")
    public String generateExpenseReport(Model model) {

        List<Expense> expenses =
                expenseService.getExpenseReport();

        model.addAttribute(
                "expenses",
                expenses
        );

        model.addAttribute(
                "totalExpenses",
                expenses.size()
        );

        model.addAttribute(
                "totalExpenseAmount",
                expenseService.getTotalExpenseAmount()
        );

        long categoryCount = expenses.stream()
                .map(Expense::getCategory)
                .filter(category ->
                        category != null &&
                                !category.isBlank())
                .distinct()
                .count();

        model.addAttribute(
                "categoryCount",
                categoryCount
        );

        return "finance/expense-report";
    }
}