package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.service.DonationService;
import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.service.BudgetService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
public class MonthlyReportController {

    private final DonationService donationService;
    private final BudgetService budgetService;

    public MonthlyReportController(
            DonationService donationService,
            BudgetService budgetService) {

        this.donationService = donationService;
        this.budgetService = budgetService;
    }

    @GetMapping("/finance/reports/monthly")
    public String generateMonthlyReport(
            @RequestParam(
                    value = "month",
                    required = false
            ) String month,

            @RequestParam(
                    value = "from",
                    required = false
            ) String from,

            Model model) {

        // =====================================================
        // SELECTED MONTH
        // =====================================================

        YearMonth selectedMonth;

        if (month == null || month.isBlank()) {

            selectedMonth = YearMonth.now();

        } else {

            selectedMonth = YearMonth.parse(month);
        }


        LocalDate startDate =
                selectedMonth.atDay(1);

        LocalDate endDate =
                selectedMonth.atEndOfMonth();


        // =====================================================
        // DONATIONS
        // =====================================================

        List<Donation> donations =
                donationService.getDonationsForMonth(
                        startDate,
                        endDate
                );


        long cashDonationCount =
                donations.stream()
                        .filter(donation ->
                                "CASH".equalsIgnoreCase(
                                        donation.getDonationType()
                                )
                        )
                        .count();


        long goodsDonationCount =
                donations.stream()
                        .filter(donation ->
                                "GOODS".equalsIgnoreCase(
                                        donation.getDonationType()
                                )
                        )
                        .count();


        double totalCashDonations =
                donations.stream()
                        .filter(donation ->
                                "CASH".equalsIgnoreCase(
                                        donation.getDonationType()
                                )
                        )
                        .mapToDouble(donation ->
                                donation.getCashAmount() != null
                                        ? donation.getCashAmount()
                                        : 0.0
                        )
                        .sum();


        // =====================================================
        // ALLOCATIONS
        // =====================================================

        List<Budget> allocatedBudgets =
                budgetService.getAllocatedBudgets();


        List<Budget> monthlyAllocationRecords =
                allocatedBudgets.stream()
                        .filter(budget ->
                                budget.getAllocatedDate() != null
                        )
                        .filter(budget -> {

                            LocalDate allocatedDate =
                                    budget.getAllocatedDate();

                            return !allocatedDate.isBefore(startDate)
                                    && !allocatedDate.isAfter(endDate);
                        })
                        .toList();


        double monthlyTotalAllocations =
                monthlyAllocationRecords.stream()
                        .mapToDouble(budget ->
                                budget.getAllocatedAmount() != null
                                        ? budget.getAllocatedAmount()
                                        : 0.0
                        )
                        .sum();


        // =====================================================
        // MODEL
        // =====================================================

        model.addAttribute(
                "selectedMonth",
                selectedMonth.toString()
        );

        model.addAttribute(
                "monthName",
                selectedMonth.getMonth().toString()
        );

        model.addAttribute(
                "year",
                selectedMonth.getYear()
        );


        model.addAttribute(
                "donations",
                donations
        );

        model.addAttribute(
                "totalDonations",
                donations.size()
        );

        model.addAttribute(
                "cashDonationCount",
                cashDonationCount
        );

        model.addAttribute(
                "goodsDonationCount",
                goodsDonationCount
        );

        model.addAttribute(
                "totalCashDonations",
                totalCashDonations
        );


        model.addAttribute(
                "monthlyTotalAllocations",
                monthlyTotalAllocations
        );

        model.addAttribute(
                "monthlyAllocationRecords",
                monthlyAllocationRecords
        );

        model.addAttribute(
                "allocationRecordCount",
                monthlyAllocationRecords.size()
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


        return "finance/monthly-report";
    }
}