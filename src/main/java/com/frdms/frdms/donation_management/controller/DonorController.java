package com.frdms.frdms.donation_management.controller;

import com.frdms.frdms.donation_management.entity.Donor;
import com.frdms.frdms.donation_management.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    // US-D01: Register donor
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("donor", new Donor());
        return "donation/donor-register";
    }

    @PostMapping("/register")
    public String registerDonor(
            @Valid @ModelAttribute("donor") Donor donor,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "donation/donor-register";
        }

        try {
            donorService.registerDonor(donor);
            return "redirect:/donors/success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "donation/donor-register";
        }
    }

    // Registration success
    @GetMapping("/success")
    public String registrationSuccess() {
        return "donation/donor-success";
    }

    // US-D04: Search donor records
    @GetMapping("/details")
    public String viewDonorDetails(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute(
                    "donors",
                    donorService.searchDonors(keyword)
            );
        } else {
            model.addAttribute(
                    "donors",
                    donorService.getAllDonors()
            );
        }

        model.addAttribute("keyword", keyword);

        return "donation/donor-details";
    }

    // US-D03: Show edit donor form
    @GetMapping("/edit/{id}")
    public String showEditDonorForm(
            @PathVariable("id") Long id,
            Model model) {

        Donor donor = donorService.getDonorById(id);

        model.addAttribute("donor", donor);

        return "donation/edit-donor";
    }

    @PostMapping("/update/{id}")
    public String updateDonor(
            @PathVariable("id") Long id,
            @ModelAttribute("donor") Donor donor) {

        donorService.updateDonor(id, donor);

        return "redirect:/donors/details";
    }
}