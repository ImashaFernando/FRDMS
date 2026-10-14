
package com.frdms.frdms.finance_management.service;

import com.frdms.frdms.distribution_management.entity.FundRequest;
import com.frdms.frdms.distribution_management.repository.FundRequestRepository;
import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.repository.BudgetRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final FundRequestRepository fundRequestRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            FundRequestRepository fundRequestRepository) {

        this.budgetRepository = budgetRepository;
        this.fundRequestRepository = fundRequestRepository;
    }

    // =========================================================
    // F07 - CREATE BUDGET REQUIREMENT
    // =========================================================

    public Budget createBudgetRequirement(Budget budget) {
        budget.setStatus("PENDING");
        return budgetRepository.save(budget);
    }

    // =========================================================
    // CREATE DISASTER BUDGET FROM FUND REQUESTS
    // =========================================================

    @Transactional
    public Budget createBudgetFromFundRequests(List<Long> fundRequestIds) {

        if (fundRequestIds == null || fundRequestIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select at least one fund request."
            );
        }

        List<FundRequest> fundRequests =
                fundRequestRepository.findByIdInAndStatus(
                        fundRequestIds,
                        "FUND_REQUESTED"
                );

        if (fundRequests.size() != fundRequestIds.size()) {
            throw new IllegalStateException(
                    "One or more selected fund requests are no longer available."
            );
        }

        String disasterName = fundRequests.get(0).getDisasterName();

        if (disasterName == null
                || disasterName.isBlank()
                || "Unknown Disaster".equalsIgnoreCase(disasterName.trim())) {

            throw new IllegalArgumentException(
                    "Selected fund requests must have a valid disaster name."
            );
        }

        disasterName = disasterName.trim();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (FundRequest fundRequest : fundRequests) {

            String requestDisaster = fundRequest.getDisasterName();

            if (requestDisaster == null
                    || !disasterName.equalsIgnoreCase(requestDisaster.trim())) {

                throw new IllegalArgumentException(
                        "All selected fund requests must belong to the same disaster."
                );
            }

            BigDecimal estimatedCost =
                    fundRequest.getEstimatedTotalCost();

            if (estimatedCost == null
                    || estimatedCost.compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "Fund Request ID " + fundRequest.getId()
                                + " has an invalid estimated cost."
                );
            }

            totalAmount = totalAmount.add(estimatedCost);
        }

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Total budget amount must be greater than zero."
            );
        }

        Budget budget = new Budget();

        budget.setDisasterName(disasterName);
        budget.setActivity("Emergency Relief");
        budget.setRequestedAmount(totalAmount.doubleValue());

        budget.setReason(
                "Budget required to fulfill relief fund requests for "
                        + disasterName + "."
        );

        budget.setStatus("PENDING");

        Budget savedBudget = budgetRepository.save(budget);

        // Link selected fund requests to the new budget
        for (FundRequest fundRequest : fundRequests) {
            fundRequest.setBudgetId(savedBudget.getId());
            fundRequest.setStatus("IN_BUDGET");
            fundRequestRepository.save(fundRequest);
        }

        return savedBudget;
    }

    // =========================================================
    // VIEW ALL BUDGETS
    // =========================================================

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    // =========================================================
    // PM - PENDING BUDGETS
    // =========================================================

    public List<Budget> getPendingBudgets() {
        return budgetRepository.findByStatus("PENDING");
    }

    // =========================================================
    // GET BUDGET BY ID
    // =========================================================

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Budget not found")
                );
    }

    // =========================================================
    // FINANCE OFFICER - UPDATE PENDING BUDGET
    // ONLY REASON CAN BE CHANGED
    // =========================================================

    public Budget updatePendingBudgetReason(Long id, String reason) {

        Budget existingBudget = getBudgetById(id);

        if (!"PENDING".equalsIgnoreCase(existingBudget.getStatus())) {
            throw new IllegalStateException(
                    "Only pending budgets can be edited."
            );
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty.");
        }

        existingBudget.setReason(reason.trim());
        existingBudget.setStatus("PENDING");

        return budgetRepository.save(existingBudget);
    }

    // =========================================================
    // FINANCE OFFICER - DELETE PENDING BUDGET
    // =========================================================

    public void deletePendingBudget(Long id) {

        Budget budget = getBudgetById(id);

        if (!"PENDING".equalsIgnoreCase(budget.getStatus())) {
            throw new IllegalStateException(
                    "Only pending budgets can be deleted."
            );
        }

        budgetRepository.delete(budget);
    }

    // =========================================================
    // PM - APPROVE BUDGET
    // =========================================================

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

    // =========================================================
    // PM - REJECT BUDGET AND RECORD REJECTION DATE
    // =========================================================

    @Transactional
    public Budget rejectBudget(Long id) {

        Budget budget = getBudgetById(id);

        if (!"PENDING".equals(budget.getStatus())) {
            throw new IllegalStateException(
                    "Only pending budgets can be rejected"
            );
        }

        // Record the rejection status and date
        budget.setStatus("REJECTED");
        budget.setRejectedDate(LocalDate.now());

        System.out.println("Rejecting budget ID: " + id);
        System.out.println(
                "Rejection date: " + budget.getRejectedDate()
        );

        Budget savedBudget = budgetRepository.save(budget);

        System.out.println(
                "Saved rejection date: " + savedBudget.getRejectedDate()
        );

        // Update only fund requests linked to this budget
        List<FundRequest> fundRequests =
                fundRequestRepository.findByBudgetId(id);

        for (FundRequest fundRequest : fundRequests) {

            if ("IN_BUDGET".equals(fundRequest.getStatus())) {
                fundRequest.setStatus("REJECTED");
                fundRequestRepository.save(fundRequest);
            }
        }

        return savedBudget;
    }

    // =========================================================
    // REJECTED BUDGETS / TRASH
    // =========================================================

    public List<Budget> getRejectedBudgets() {
        return budgetRepository.findByStatus("REJECTED");
    }

    // =========================================================
    // F08 - APPROVED BUDGETS
    // =========================================================

    public List<Budget> getApprovedBudgets() {
        return budgetRepository.findByStatus("APPROVED");
    }

    // =========================================================
    // F08 - ALLOCATE BUDGET
    // =========================================================

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
        budget.setAllocatedDate(LocalDate.now());
        budget.setStatus("ALLOCATED");

        return budgetRepository.save(budget);
    }

    // =========================================================
    // DISTRIBUTION - ALLOCATED FUNDS
    // =========================================================

    public List<Budget> getAllocatedBudgets() {
        return budgetRepository.findByStatus("ALLOCATED");
    }

    // =========================================================
    // AVAILABLE BUDGET
    // =========================================================

    public double getAvailableBudget() {

        return budgetRepository.findByStatus("APPROVED")
                .stream()
                .mapToDouble(budget -> {

                    double requested =
                            budget.getRequestedAmount() != null
                                    ? budget.getRequestedAmount()
                                    : 0.0;

                    double allocated =
                            budget.getAllocatedAmount() != null
                                    ? budget.getAllocatedAmount()
                                    : 0.0;

                    return requested - allocated;
                })
                .sum();
    }

    // =========================================================
    // F13 - BUDGET REPORT
    // =========================================================

    public List<Budget> getBudgetReport() {
        return budgetRepository.findAll();
    }
}
