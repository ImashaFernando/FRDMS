package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.entity.Expense;
import com.frdms.frdms.finance_management.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/finance/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // CREATE
    @GetMapping("/create")
    public String showCreateExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "finance/create-expense";
    }

    @PostMapping("/create")
    public String createExpense(
            @Valid @ModelAttribute("expense") Expense expense,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "finance/create-expense";
        }

        expenseService.createExpense(expense);

        return "redirect:/finance/expenses";
    }

    // VIEW + SEARCH
    @GetMapping
    public String viewExpenses(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute(
                    "expenses",
                    expenseService.searchExpenses(keyword)
            );
        } else {
            model.addAttribute(
                    "expenses",
                    expenseService.getAllExpenses()
            );
        }

        model.addAttribute("keyword", keyword);

        return "finance/expense-records";
    }

    // EDIT PAGE
    @GetMapping("/edit/{id}")
    public String showEditExpenseForm(
            @PathVariable("id") Long id,
            Model model) {

        Expense expense = expenseService.getExpenseById(id);

        model.addAttribute("expense", expense);

        return "finance/edit-expense";
    }

    // UPDATE
    @PostMapping("/update/{id}")
    public String updateExpense(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("expense") Expense expense,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "finance/edit-expense";
        }

        expenseService.updateExpense(id, expense);

        return "redirect:/finance/expenses";
    }

    // DELETE
    @PostMapping("/delete/{id}")
    public String deleteExpense(
            @PathVariable("id") Long id) {

        expenseService.deleteExpense(id);

        return "redirect:/finance/expenses";
    }

    // CATEGORIZE
    @PostMapping("/categorize/{id}")
    public String categorizeExpense(
            @PathVariable("id") Long id,
            @RequestParam("category") String category) {

        expenseService.categorizeExpense(id, category);

        return "redirect:/finance/expenses";
    }
}