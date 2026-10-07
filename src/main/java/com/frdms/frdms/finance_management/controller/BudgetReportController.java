package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.service.DonationService;
import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.service.BudgetService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BudgetReportController {

    private final BudgetService budgetService;
    private final DonationService donationService;

    public BudgetReportController(
            BudgetService budgetService,
            DonationService donationService) {

        this.budgetService = budgetService;
        this.donationService = donationService;
    }

    @GetMapping("/finance/reports/budgets")
    public String generateBudgetReport(
            @RequestParam(
                    value = "from",
                    required = false
            ) String from,
            Model model) {

        // =====================================================
        // BUDGETS
        // =====================================================

        List<Budget> budgets =
                budgetService.getBudgetReport();

        model.addAttribute("budgets", budgets);


        // =====================================================
        // TOTAL REQUIREMENTS
        // =====================================================

        model.addAttribute(
                "totalRequirements",
                budgets.size()
        );


        // =====================================================
        // PENDING
        // =====================================================

        long pendingCount = budgets.stream()
                .filter(budget ->
                        "PENDING".equalsIgnoreCase(
                                budget.getStatus()
                        ))
                .count();

        model.addAttribute(
                "pendingCount",
                pendingCount
        );


        // =====================================================
        // APPROVED
        // =====================================================

        long approvedCount = budgets.stream()
                .filter(budget ->
                        "APPROVED".equalsIgnoreCase(
                                budget.getStatus()
                        ))
                .count();

        model.addAttribute(
                "approvedCount",
                approvedCount
        );


        // =====================================================
        // ALLOCATED COUNT
        // =====================================================

        long allocatedCount = budgets.stream()
                .filter(budget ->
                        "ALLOCATED".equalsIgnoreCase(
                                budget.getStatus()
                        ))
                .count();

        model.addAttribute(
                "allocatedCount",
                allocatedCount
        );


        // =====================================================
        // TOTAL REQUESTED
        // =====================================================

        double totalRequestedAmount =
                budgets.stream()
                        .mapToDouble(budget ->
                                budget.getRequestedAmount() != null
                                        ? budget.getRequestedAmount()
                                        : 0.0
                        )
                        .sum();

        model.addAttribute(
                "totalRequestedAmount",
                totalRequestedAmount
        );


        // =====================================================
        // TOTAL ALLOCATED
        // =====================================================

        double totalAllocatedAmount =
                budgets.stream()
                        .filter(budget ->
                                "ALLOCATED".equalsIgnoreCase(
                                        budget.getStatus()
                                ))
                        .mapToDouble(budget ->
                                budget.getAllocatedAmount() != null
                                        ? budget.getAllocatedAmount()
                                        : 0.0
                        )
                        .sum();

        model.addAttribute(
                "totalAllocatedAmount",
                totalAllocatedAmount
        );


        // =====================================================
        // TOTAL CASH DONATIONS
        // =====================================================

        List<Donation> allDonations =
                donationService.getAllDonations();

        double totalCashDonationFunds =
                allDonations.stream()
                        .filter(donation ->
                                donation.getDonationType() != null
                                        && "CASH".equalsIgnoreCase(
                                        donation.getDonationType().trim()
                                )
                        )
                        .mapToDouble(donation ->
                                donation.getCashAmount() != null
                                        ? donation.getCashAmount()
                                        : 0.0
                        )
                        .sum();

        model.addAttribute(
                "totalCashDonationFunds",
                totalCashDonationFunds
        );


        // =====================================================
        // AVAILABLE TOTAL FUNDS
        // =====================================================

        double availableTotalFunds =
                totalCashDonationFunds
                        - totalAllocatedAmount;

        if (availableTotalFunds < 0) {
            availableTotalFunds = 0.0;
        }

        model.addAttribute(
                "availableTotalFunds",
                availableTotalFunds
        );


        // =====================================================
        // BACK URL
        // =====================================================

        if ("pm".equalsIgnoreCase(from)) {

            model.addAttribute(
                    "backUrl",
                    "/pm/dashboard"
            );

            model.addAttribute(
                    "backText",
                    "Back to PM Dashboard"
            );

        } else {

            model.addAttribute(
                    "backUrl",
                    "/finance/dashboard"
            );

            model.addAttribute(
                    "backText",
                    "Back to Finance Dashboard"
            );
        }


        return "finance/budget-report";
    }
}