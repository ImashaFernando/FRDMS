package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.service.DonationService;

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

    public MonthlyReportController(
            DonationService donationService) {

        this.donationService = donationService;
    }

    // =========================
    // F14 - Monthly Financial Report
    // =========================
    @GetMapping("/finance/reports/monthly")
    public String generateMonthlyReport(
            @RequestParam(
                    value = "month",
                    required = false
            ) String month,
            Model model) {

        // If no month is selected,
        // use the current month.
        YearMonth selectedMonth;

        if (month == null || month.isBlank()) {

            selectedMonth = YearMonth.now();

        } else {

            selectedMonth = YearMonth.parse(month);
        }

        // First day of selected month
        LocalDate startDate =
                selectedMonth.atDay(1);

        // Last day of selected month
        LocalDate endDate =
                selectedMonth.atEndOfMonth();

        // =========================
        // DONATIONS
        // =========================

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

        // =========================
        // SEND DATA TO HTML
        // =========================

        model.addAttribute(
                "selectedMonth",
                selectedMonth.toString()
        );

        model.addAttribute(
                "monthName",
                selectedMonth.getMonth()
                        .toString()
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

        return "finance/monthly-report";
    }
}