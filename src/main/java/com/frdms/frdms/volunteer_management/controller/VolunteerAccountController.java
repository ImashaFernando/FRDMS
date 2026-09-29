package com.frdms.frdms.volunteer_management.controller;

import com.frdms.frdms.volunteer_management.entity.VolunteerAccount;
import com.frdms.frdms.volunteer_management.service.VolunteerAccountService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VolunteerAccountController {

    private final VolunteerAccountService volunteerAccountService;

    public VolunteerAccountController(
            VolunteerAccountService volunteerAccountService
    ) {
        this.volunteerAccountService = volunteerAccountService;
    }

    // =========================
    // ACCOUNT REGISTRATION
    // =========================

    @GetMapping("/volunteer/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute(
                "volunteerAccount",
                new VolunteerAccount()
        );

        return "volunteer/account-register";
    }

    @PostMapping("/volunteer/register")
    public String registerVolunteer(
            @Valid VolunteerAccount volunteerAccount,
            BindingResult bindingResult,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model
    ) {

        // Validation errors
        if (bindingResult.hasErrors()) {
            return "volunteer/account-register";
        }

        // Password confirmation
        if (!volunteerAccount.getPassword().equals(confirmPassword)) {

            model.addAttribute(
                    "passwordError",
                    "Password and confirm password do not match."
            );

            return "volunteer/account-register";
        }

        // Check duplicate email
        if (volunteerAccountService.emailExists(
                volunteerAccount.getEmail())) {

            model.addAttribute(
                    "emailError",
                    "An account with this email already exists."
            );

            return "volunteer/account-register";
        }

        try {

            // Create volunteer account
            volunteerAccountService
                    .registerVolunteerAccount(volunteerAccount);

            // SUCCESS → COMMON LOGIN PAGE
            return "redirect:/login?registered=true";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "registrationError",
                    exception.getMessage()
            );

            return "volunteer/account-register";
        }
    }
}