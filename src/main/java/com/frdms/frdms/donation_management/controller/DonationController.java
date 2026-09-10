package com.frdms.frdms.donation_management.controller;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.service.DonationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    // US-D05: Show donation submission form
    @GetMapping("/submit")
    public String showDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        return "donation/donation-submit";
    }

    // US-D05: Submit cash or goods donation
    @PostMapping("/submit")
    public String submitDonation(
            @Valid @ModelAttribute("donation") Donation donation,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "donation/donation-submit";
        }

        donationService.submitDonation(donation);

        return "redirect:/donations/success";
    }

    // US-D06: View recorded cash donations
    @GetMapping("/cash")
    public String viewCashDonations(Model model) {

        model.addAttribute(
                "cashDonations",
                donationService.getCashDonations()
        );

        return "donation/cash-donations";
    }

    // US-D07: View recorded goods donations
    @GetMapping("/goods")
    public String viewGoodsDonations(Model model) {

        model.addAttribute(
                "goodsDonations",
                donationService.getGoodsDonations()
        );

        return "donation/goods-donations";
    }

    // US-D05 success page
    @GetMapping("/success")
    public String donationSuccess() {
        return "donation/donation-success";
    }
}