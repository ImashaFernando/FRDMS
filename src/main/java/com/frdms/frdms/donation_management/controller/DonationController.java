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

    @GetMapping("/submit")
    public String showDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        return "donation/donation-submit";
    }

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

    @GetMapping("/success")
    public String donationSuccess() {
        return "donation/donation-success";
    }
}