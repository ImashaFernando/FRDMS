package com.frdms.frdms.finance_management.service;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.repository.DonationRepository;
import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.entity.Expense;
import com.frdms.frdms.finance_management.repository.BudgetRepository;
import com.frdms.frdms.finance_management.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinanceDashboardService {

    private final DonationRepository donationRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public FinanceDashboardService(
            DonationRepository donationRepository,
            ExpenseRepository expenseRepository,
            BudgetRepository budgetRepository
    ) {
        this.donationRepository = donationRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    // =========================
    // TOTAL DONATIONS
    // =========================
    public double getTotalDonations() {

        return donationRepository.findAll()
                .stream()
                .map(Donation::getCashAmount)
                .filter(amount -> amount != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    // =========================
    // TOTAL EXPENSES
    // =========================
    public double getTotalExpenses() {

        return expenseRepository.findAll()
                .stream()
                .map(Expense::getAmount)
                .filter(amount -> amount != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    // =========================
    // TOTAL ALLOCATED BUDGET
    // =========================
    public double getTotalAllocatedBudget() {

        return budgetRepository.findAll()
                .stream()
                .filter(budget ->
                        "ALLOCATED".equalsIgnoreCase(budget.getStatus())
                )
                .map(Budget::getAllocatedAmount)
                .filter(amount -> amount != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    // =========================
    // AVAILABLE BUDGET
    // =========================
    public double getAvailableBudget() {

        double allocatedBudget = getTotalAllocatedBudget();
        double totalExpenses = getTotalExpenses();

        return Math.max(allocatedBudget - totalExpenses, 0);
    }

    // =========================
    // PENDING REQUESTS
    // =========================
    public long getPendingRequests() {

        return budgetRepository.findByStatus("PENDING")
                .size();
    }

    // =========================
    // APPROVED BUDGETS
    // =========================
    public List<Budget> getApprovedBudgets() {

        return budgetRepository.findByStatus("APPROVED");
    }

    // =========================
    // ALLOCATED BUDGETS
    // =========================
    public List<Budget> getAllocatedBudgets() {

        return budgetRepository.findByStatus("ALLOCATED");
    }

    // =========================
    // RECENT EXPENSES
    // =========================
    public List<Expense> getRecentExpenses() {

        return expenseRepository.findAll()
                .stream()
                .filter(expense -> expense.getExpenseDate() != null)
                .sorted((a, b) ->
                        b.getExpenseDate()
                                .compareTo(a.getExpenseDate())
                )
                .limit(5)
                .toList();
    }

    // =========================
    // RECENT DONATIONS
    // =========================
    public List<Donation> getRecentDonations() {

        return donationRepository.findAll()
                .stream()
                .filter(donation -> donation.getDonationDate() != null)
                .sorted((a, b) ->
                        b.getDonationDate()
                                .compareTo(a.getDonationDate())
                )
                .limit(5)
                .toList();
    }
}