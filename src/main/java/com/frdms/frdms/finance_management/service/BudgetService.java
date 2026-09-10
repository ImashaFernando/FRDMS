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

    // US-F07: Create budget requirement
    public Budget createBudgetRequirement(Budget budget) {

        budget.setStatus("PENDING");

        return budgetRepository.save(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public List<Budget> getPendingBudgets() {
        return budgetRepository.findByStatus("PENDING");
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Budget not found"));
    }
}