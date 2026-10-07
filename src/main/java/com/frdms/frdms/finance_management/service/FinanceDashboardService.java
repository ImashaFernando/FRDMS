package com.frdms.frdms.finance_management.service;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.repository.DonationRepository;
import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinanceDashboardService {

    private final DonationRepository donationRepository;
    private final BudgetRepository budgetRepository;

    public FinanceDashboardService(
            DonationRepository donationRepository,
            BudgetRepository budgetRepository
    ) {
        this.donationRepository = donationRepository;
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