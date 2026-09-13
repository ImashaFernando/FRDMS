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

    // =========================
    // F07 - Create Budget Requirement
    // =========================

    public Budget createBudgetRequirement(Budget budget) {
        budget.setStatus("PENDING");
        return budgetRepository.save(budget);
    }

    // =========================
    // View All Budgets
    // =========================

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    // =========================
    // PM - Pending Budgets
    // =========================

    public List<Budget> getPendingBudgets() {
        return budgetRepository.findByStatus("PENDING");
    }

    // =========================
    // Get Budget By ID
    // =========================

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Budget not found"));
    }

    // =========================
    // PM - Approve Budget
    // =========================

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

    // =========================
    // PM - Reject Budget
    // =========================

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

    // =========================
    // Rejected Budgets / Trash
    // =========================

    public List<Budget> getRejectedBudgets() {
        return budgetRepository.findByStatus("REJECTED");
    }

    // =========================
    // F08 - Approved Budgets
    // =========================

    public List<Budget> getApprovedBudgets() {
        return budgetRepository.findByStatus("APPROVED");
    }

    // =========================
    // F08 - Allocate Budget
    // =========================

    public Budget allocateBudget(Long id, Double allocatedAmount) {

        Budget budget = getBudgetById(id);

        if (!"APPROVED".equals(budget.getStatus())) {
            throw new IllegalStateException(
                    "Only approved budgets can be allocated"
            );
        }

        if (allocatedAmount == null || allocatedAmount <= 0) {
            throw new IllegalArgumentException(
                    "Allocated amount must be greater than 0"
            );
        }

        if (allocatedAmount > budget.getRequestedAmount()) {
            throw new IllegalArgumentException(
                    "Allocated amount cannot exceed requested amount"
            );
        }

        budget.setAllocatedAmount(allocatedAmount);
        budget.setAllocatedDate(java.time.LocalDate.now());
        budget.setStatus("ALLOCATED");

        return budgetRepository.save(budget);
    }
}