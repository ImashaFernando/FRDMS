package com.frdms.frdms.finance_management.service;

import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    // F07 - Create Budget
    public Budget createBudgetRequirement(Budget budget) {

        budget.setStatus("PENDING");

        return budgetRepository.save(budget);
    }

    // View all budgets
    public List<Budget> getAllBudgets() {

        return budgetRepository.findAll();
    }

    // View pending budgets
    public List<Budget> getPendingBudgets() {

        return budgetRepository.findByStatus("PENDING");
    }

    // Find budget
    public Budget getBudgetById(Long id) {

        return budgetRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("Budget not found")
                );
    }

    // PM approves budget
    public Budget approveBudget(Long id) {

        Budget budget = getBudgetById(id);

        if (!"PENDING".equals(budget.getStatus())) {
            throw new IllegalStateException(
                    "Only pending budgets can be approved"
            );
        }

        budget.setStatus("APPROVED");

        return budgetRepository.save(budget);
    }

    // PM rejects budget
    public Budget rejectBudget(Long id) {

        Budget budget = getBudgetById(id);

        if (!"PENDING".equals(budget.getStatus())) {
            throw new IllegalStateException(
                    "Only pending budgets can be rejected"
            );
        }

        budget.setStatus("REJECTED");

        return budgetRepository.save(budget);
    }

    // View rejected budgets
    public List<Budget> getRejectedBudgets() {

        return budgetRepository.findByStatus("REJECTED");
    }
}