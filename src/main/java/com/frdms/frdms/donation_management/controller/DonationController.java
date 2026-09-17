package com.frdms.frdms.donation_management.controller;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.entity.Donor;
import com.frdms.frdms.donation_management.repository.DonorRepository;
import com.frdms.frdms.donation_management.service.DonationService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;
    private final DonorRepository donorRepository;

    public DonationController(
            DonationService donationService,
            DonorRepository donorRepository) {

        this.donationService = donationService;
        this.donorRepository = donorRepository;
    }


    // =====================================
    // DONOR - SUBMIT DONATION
    // =====================================

    @GetMapping("/submit")
    public String showDonationForm(
            Authentication authentication,
            Model model) {

        Donor donor = getLoggedInDonor(authentication);

        Donation donation = new Donation();

        donation.setDonorId(donor.getId());

        model.addAttribute("donation", donation);

        return "donation/donation-submit";
    }


    @PostMapping("/submit")
    public String submitDonation(
            @Valid @ModelAttribute("donation") Donation donation,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            return "donation/donation-submit";
        }

        Donor donor = getLoggedInDonor(authentication);

        // Never trust donorId from the form
        donation.setDonorId(donor.getId());

        donationService.submitDonation(donation);

        return "redirect:/donor/dashboard";
    }


    // =====================================
    // DONOR - MY DONATIONS
    // =====================================

    @GetMapping("/my")
    public String viewMyDonations(
            Authentication authentication,
            Model model) {

        Donor donor = getLoggedInDonor(authentication);

        List<Donation> donations =
                donationService.getDonationsByDonor(donor.getId());

        model.addAttribute("donations", donations);

        model.addAttribute(
                "today",
                java.time.LocalDate.now()
        );

        return "donation/my-donations";
    }


    // =====================================
    // DONOR - EDIT
    // =====================================

    @GetMapping("/edit/{id}")
    public String showEditDonationForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Donor donor = getLoggedInDonor(authentication);

        Donation donation =
                donationService.getDonationById(id);

        // Own donation check
        if (!donation.getDonorId().equals(donor.getId())) {
            return "redirect:/donations/my";
        }

        // 2-day check
        if (!donationService.isWithinEditPeriod(donation)) {
            return "redirect:/donations/my";
        }

        model.addAttribute("donation", donation);

        return "donation/edit-donation";
    }


    @PostMapping("/update/{id}")
    public String updateDonation(
            @PathVariable Long id,
            @Valid @ModelAttribute("donation") Donation donation,
            BindingResult bindingResult,
            Authentication authentication,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "donation/edit-donation";
        }

        Donor donor = getLoggedInDonor(authentication);

        try {

            donationService.updateDonation(
                    id,
                    donor.getId(),
                    donation
            );

            return "redirect:/donations/my";

        } catch (IllegalStateException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "donation",
                    donationService.getDonationById(id)
            );

            return "donation/edit-donation";
        }
    }


    // =====================================
    // DONOR - DELETE
    // =====================================

    @PostMapping("/delete/{id}")
    public String deleteDonation(
            @PathVariable Long id,
            Authentication authentication) {

        Donor donor = getLoggedInDonor(authentication);

        try {

            donationService.deleteDonation(
                    id,
                    donor.getId()
            );

        } catch (IllegalStateException e) {

            // If expired or not owned,
            // simply return to own donations
        }

        return "redirect:/donations/my";
    }


    // =====================================
    // DONATION RECEIPT
    // =====================================

    @GetMapping("/receipt/{id}")
    public String generateDonationReceipt(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Donor donor = getLoggedInDonor(authentication);

        Donation donation =
                donationService.getDonationById(id);

        if (!donation.getDonorId().equals(donor.getId())) {
            return "redirect:/donations/my";
        }

        model.addAttribute("donation", donation);

        return "donation/donation-receipt";
    }


    // =====================================
    // SUCCESS
    // =====================================

    @GetMapping("/success")
    public String donationSuccess() {
        return "donation/donation-success";
    }


    // =====================================
    // HELPER
    // =====================================

    private Donor getLoggedInDonor(
            Authentication authentication) {

        String username = authentication.getName();

        return donorRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Logged-in donor not found"
                        ));
    }


    // =====================================
    // FINANCE OFFICER - CASH DONATIONS
    // =====================================

    @GetMapping("/cash")
    public String viewCashDonations(Model model) {

        model.addAttribute(
                "cashDonations",
                donationService.getCashDonations()
        );

        return "donation/cash-donations";
    }


    // =====================================
    // FINANCE OFFICER - GOODS DONATIONS
    // =====================================

    @GetMapping("/goods")
    public String viewGoodsDonations(Model model) {

        model.addAttribute(
                "goodsDonations",
                donationService.getGoodsDonations()
        );

        return "donation/goods-donations";
    }


    // =====================================
    // FINANCE OFFICER - ACCEPT DONATION
    // =====================================

    @PostMapping("/accept/{id}")
    public String acceptDonation(
            @PathVariable Long id,
            @RequestParam String type) {

        donationService.acceptDonation(id);

        if ("GOODS".equalsIgnoreCase(type)) {
            return "redirect:/donations/goods";
        }

        return "redirect:/donations/cash";
    }


    // =====================================
    // FINANCE OFFICER - REJECT DONATION
    // =====================================

    @PostMapping("/reject/{id}")
    public String rejectDonation(
            @PathVariable Long id,
            @RequestParam String type) {

        donationService.rejectDonation(id);

        if ("GOODS".equalsIgnoreCase(type)) {
            return "redirect:/donations/goods";
        }

        return "redirect:/donations/cash";
    }
}