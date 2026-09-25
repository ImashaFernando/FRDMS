package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.entity.Expense;
import com.frdms.frdms.finance_management.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PmExpenseController {

    private final ExpenseService expenseService;

    public PmExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // PM - View expenses only
    @GetMapping("/pm/expenses")
    public String viewExpenses(Model model) {

        List<Expense> expenses = expenseService.getExpenseReport();

        model.addAttribute("expenses", expenses);

        return "finance/pm-expense-records";
    }
}
