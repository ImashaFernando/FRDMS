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


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public BudgetService(
            BudgetRepository budgetRepository,
            FundRequestRepository fundRequestRepository) {

        this.budgetRepository = budgetRepository;

        this.fundRequestRepository =
                fundRequestRepository;
    }


    // =========================================================
    // F07 - CREATE BUDGET REQUIREMENT
    // =========================================================

    public Budget createBudgetRequirement(Budget budget) {

        // New budget requests always start as PENDING
        budget.setStatus("PENDING");

        return budgetRepository.save(budget);
    }


    // =========================================================
    // CREATE DISASTER BUDGET FROM FUND REQUESTS
    // =========================================================

    @Transactional
    public Budget createBudgetFromFundRequests(
            List<Long> fundRequestIds) {


        // -----------------------------------------------------
        // CHECK SELECTION
        // -----------------------------------------------------

        if (fundRequestIds == null ||
                fundRequestIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select at least one fund request."
            );
        }


        // -----------------------------------------------------
        // GET ONLY VALID PENDING FUND REQUESTS
        // -----------------------------------------------------

        List<FundRequest> fundRequests =
                fundRequestRepository
                        .findByIdInAndStatus(
                                fundRequestIds,
                                "FUND_REQUESTED"
                        );


        // -----------------------------------------------------
        // CHECK ALL REQUESTS WERE FOUND
        // -----------------------------------------------------

        if (fundRequests.size()
                != fundRequestIds.size()) {

            throw new IllegalStateException(
                    "One or more selected fund requests " +
                            "are no longer available."
            );
        }


        // -----------------------------------------------------
        // GET DISASTER NAME
        // -----------------------------------------------------

        String disasterName =
                fundRequests.get(0).getDisasterName();


        if (disasterName == null ||
                disasterName.isBlank() ||
                "Unknown Disaster".equalsIgnoreCase(
                        disasterName.trim())) {

            throw new IllegalArgumentException(
                    "Selected fund requests must have " +
                            "a valid disaster name."
            );
        }


        disasterName = disasterName.trim();


        // -----------------------------------------------------
        // CALCULATE TOTAL
        // -----------------------------------------------------

        BigDecimal totalAmount =
                BigDecimal.ZERO;


        for (FundRequest fundRequest :
                fundRequests) {


            // -------------------------------------------------
            // CHECK SAME DISASTER
            // -------------------------------------------------

            String requestDisaster =
                    fundRequest.getDisasterName();


            if (requestDisaster == null ||
                    !disasterName.equalsIgnoreCase(
                            requestDisaster.trim())) {

                throw new IllegalArgumentException(
                        "All selected fund requests must " +
                                "belong to the same disaster."
                );
            }


            // -------------------------------------------------
            // CHECK ESTIMATED COST
            // -------------------------------------------------

            BigDecimal estimatedCost =
                    fundRequest.getEstimatedTotalCost();


            if (estimatedCost == null ||
                    estimatedCost.compareTo(
                            BigDecimal.ZERO
                    ) <= 0) {

                throw new IllegalArgumentException(
                        "Fund Request ID " +
                                fundRequest.getId() +
                                " has an invalid estimated cost."
                );
            }


            // -------------------------------------------------
            // ADD TO TOTAL
            // -------------------------------------------------

            totalAmount =
                    totalAmount.add(
                            estimatedCost
                    );
        }


        // -----------------------------------------------------
        // CHECK TOTAL
        // -----------------------------------------------------

        if (totalAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "Total budget amount must be greater than zero."
            );
        }


        // -----------------------------------------------------
        // CREATE BUDGET
        // -----------------------------------------------------

        Budget budget = new Budget();


        budget.setDisasterName(
                disasterName
        );


        budget.setActivity(
                "Emergency Relief"
        );


        budget.setRequestedAmount(
                totalAmount.doubleValue()
        );


        budget.setReason(
                "Budget required to fulfill relief " +
                        "fund requests for " +
                        disasterName + "."
        );


        // -----------------------------------------------------
        // SEND TO PROJECT MANAGER
        // -----------------------------------------------------

        budget.setStatus(
                "PENDING"
        );


        Budget savedBudget =
                budgetRepository.save(
                        budget
                );


        // -----------------------------------------------------
        // MARK FUND REQUESTS AS IN BUDGET
        // -----------------------------------------------------

        for (FundRequest fundRequest :
                fundRequests) {

            fundRequest.setStatus(
                    "IN_BUDGET"
            );

            fundRequestRepository.save(
                    fundRequest
            );
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

        return budgetRepository.findByStatus(
                "PENDING"
        );
    }


    // =========================================================
    // GET BUDGET BY ID
    // =========================================================

    public Budget getBudgetById(Long id) {

        return budgetRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Budget not found"
                        )
                );
    }


    // =========================================================
    // FINANCE OFFICER - UPDATE PENDING BUDGET
    // ONLY REASON CAN BE CHANGED
    // =========================================================

    public Budget updatePendingBudgetReason(
            Long id,
            String reason) {

        // -----------------------------------------------------
        // GET EXISTING BUDGET
        // -----------------------------------------------------

        Budget existingBudget =
                getBudgetById(id);


        // -----------------------------------------------------
        // ONLY PENDING BUDGETS CAN BE EDITED
        // -----------------------------------------------------

        if (!"PENDING".equalsIgnoreCase(
                existingBudget.getStatus())) {

            throw new IllegalStateException(
                    "Only pending budgets can be edited."
            );
        }


        // -----------------------------------------------------
        // VALIDATE REASON
        // -----------------------------------------------------

        if (reason == null ||
                reason.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Reason cannot be empty."
            );
        }


        // -----------------------------------------------------
        // UPDATE ONLY REASON
        // -----------------------------------------------------

        existingBudget.setReason(
                reason.trim()
        );


        // -----------------------------------------------------
        // KEEP STATUS AS PENDING
        // -----------------------------------------------------

        existingBudget.setStatus(
                "PENDING"
        );


        // -----------------------------------------------------
        // SAVE EXISTING BUDGET
        // -----------------------------------------------------

        return budgetRepository.save(
                existingBudget
        );
    }


    // =========================================================
    // FINANCE OFFICER - DELETE PENDING BUDGET
    // =========================================================

    public void deletePendingBudget(Long id) {

        Budget budget =
                getBudgetById(id);


        // -----------------------------------------------------
        // ONLY PENDING BUDGETS CAN BE DELETED
        // -----------------------------------------------------

        if (!"PENDING".equalsIgnoreCase(
                budget.getStatus())) {

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

        Budget budget =
                getBudgetById(id);


        // -----------------------------------------------------
        // ONLY PENDING BUDGETS CAN BE APPROVED
        // -----------------------------------------------------

        if (!"PENDING".equals(
                budget.getStatus())) {

            throw new IllegalStateException(
                    "Only pending budgets can be approved"
            );
        }


        budget.setStatus(
                "APPROVED"
        );


        return budgetRepository.save(
                budget
        );
    }


    // =========================================================
    // PM - REJECT BUDGET
    // =========================================================

    public Budget rejectBudget(Long id) {

        Budget budget =
                getBudgetById(id);


        // -----------------------------------------------------
        // ONLY PENDING BUDGETS CAN BE REJECTED
        // -----------------------------------------------------

        if (!"PENDING".equals(
                budget.getStatus())) {

            throw new IllegalStateException(
                    "Only pending budgets can be rejected"
            );
        }


        budget.setStatus(
                "REJECTED"
        );


        return budgetRepository.save(
                budget
        );
    }


    // =========================================================
    // REJECTED BUDGETS / TRASH
    // =========================================================

    public List<Budget> getRejectedBudgets() {

        return budgetRepository.findByStatus(
                "REJECTED"
        );
    }


    // =========================================================
    // F08 - APPROVED BUDGETS
    // =========================================================

    public List<Budget> getApprovedBudgets() {

        return budgetRepository.findByStatus(
                "APPROVED"
        );
    }


    // =========================================================
    // F08 - ALLOCATE BUDGET
    // =========================================================

    public Budget allocateBudget(
            Long id,
            Double allocatedAmount) {

        Budget budget =
                getBudgetById(id);


        // -----------------------------------------------------
        // ONLY APPROVED BUDGETS CAN BE ALLOCATED
        // -----------------------------------------------------

        if (!"APPROVED".equals(
                budget.getStatus())) {

            throw new IllegalStateException(
                    "Only approved budgets can be allocated"
            );
        }


        // -----------------------------------------------------
        // VALIDATE ALLOCATED AMOUNT
        // -----------------------------------------------------

        if (allocatedAmount == null ||
                allocatedAmount <= 0) {

            throw new IllegalArgumentException(
                    "Allocated amount must be greater than 0"
            );
        }


        // -----------------------------------------------------
        // ALLOCATED AMOUNT CANNOT EXCEED REQUESTED AMOUNT
        // -----------------------------------------------------

        if (allocatedAmount >
                budget.getRequestedAmount()) {

            throw new IllegalArgumentException(
                    "Allocated amount cannot exceed " +
                            "requested amount"
            );
        }


        // -----------------------------------------------------
        // SAVE ALLOCATION
        // -----------------------------------------------------

        budget.setAllocatedAmount(
                allocatedAmount
        );


        budget.setAllocatedDate(
                LocalDate.now()
        );


        budget.setStatus(
                "ALLOCATED"
        );


        return budgetRepository.save(
                budget
        );
    }


    // =========================================================
    // DISTRIBUTION - ALLOCATED FUNDS
    // =========================================================

    public List<Budget> getAllocatedBudgets() {

        return budgetRepository.findByStatus(
                "ALLOCATED"
        );
    }


    // =========================================================
    // AVAILABLE BUDGET
    // =========================================================

    public double getAvailableBudget() {

        return budgetRepository
                .findByStatus("APPROVED")
                .stream()
                .mapToDouble(budget -> {

                    double requested =
                            budget.getRequestedAmount()
                                    != null
                                    ? budget.getRequestedAmount()
                                    : 0.0;

                    double allocated =
                            budget.getAllocatedAmount()
                                    != null
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