package com.frdms.frdms.donation_management.controller;

import com.frdms.frdms.donation_management.entity.Donor;
import com.frdms.frdms.donation_management.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

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

    @GetMapping("/success")
    public String registrationSuccess() {
        return "donation/donor-success";
    }
}