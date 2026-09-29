package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.donation_management.service.DonationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DonationReportController {

    private final DonationService donationService;

    public DonationReportController(DonationService donationService) {
        this.donationService = donationService;
    }

    // US-F11 - Generate Donation Report
    @GetMapping("/finance/reports/donations")
    public String donationReport(Model model) {

        model.addAttribute(
                "donations",
                donationService.getAllDonations()
        );

        model.addAttribute(
                "totalDonations",
                donationService.getTotalDonationCount()
        );

        model.addAttribute(
                "cashDonationAmount",
                donationService.getTotalCashDonationAmount()
        );

        model.addAttribute(
                "goodsDonations",
                donationService.getTotalGoodsDonationCount()
        );

        model.addAttribute(
                "pendingDonations",
                donationService.getPendingDonationCount()
        );

        return "finance/donation-report";
    }
}