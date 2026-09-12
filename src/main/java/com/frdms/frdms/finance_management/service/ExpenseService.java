package com.frdms.frdms.finance_management.service;

import com.frdms.frdms.finance_management.entity.Expense;
import com.frdms.frdms.finance_management.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // F01 - Create Expense
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    // F02 - View Expenses
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
    }

    // F03 - Update Expense
    public Expense updateExpense(Long id, Expense updatedExpense) {

        Expense existingExpense = getExpenseById(id);

        existingExpense.setExpenseDate(updatedExpense.getExpenseDate());
        existingExpense.setDescription(updatedExpense.getDescription());
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setCategory(updatedExpense.getCategory());

        return expenseRepository.save(existingExpense);
    }

    // F04 - Delete Expense
    public void deleteExpense(Long id) {

        if (!expenseRepository.existsById(id)) {
            throw new IllegalArgumentException("Expense not found");
        }

        expenseRepository.deleteById(id);
    }

    // F05 - Search Expenses
    public List<Expense> searchExpenses(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllExpenses();
        }

        return expenseRepository
                .findByDescriptionContainingIgnoreCase(keyword.trim());
    }

    // F06 - Categorize Expenses
    public Expense categorizeExpense(Long id, String category) {

        Expense expense = getExpenseById(id);

        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }

        expense.setCategory(category.trim());

        return expenseRepository.save(expense);
    }

    // View expenses by category
    public List<Expense> getExpensesByCategory(String category) {

        if (category == null || category.trim().isEmpty()) {
            return getAllExpenses();
        }

        return expenseRepository.findByCategory(category);
    }
}