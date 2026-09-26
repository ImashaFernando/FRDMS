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

        if (bindingResult.hasErrors()) {
            return "volunteer/account-register";
        }

        if (!volunteerAccount.getPassword().equals(confirmPassword)) {

            model.addAttribute(
                    "passwordError",
                    "Password and confirm password do not match."
            );

            return "volunteer/account-register";
        }

        if (volunteerAccountService.emailExists(volunteerAccount.getEmail())) {

            model.addAttribute(
                    "emailError",
                    "An account with this email already exists."
            );

            return "volunteer/account-register";
        }

        try {

            volunteerAccountService
                    .registerVolunteerAccount(volunteerAccount);

            model.addAttribute(
                    "successMessage",
                    "Account created successfully. You can now login."
            );

            model.addAttribute(
                    "volunteerAccount",
                    new VolunteerAccount()
            );

            return "volunteer/account-register";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "registrationError",
                    exception.getMessage()
            );

            return "volunteer/account-register";
        }
    }

    // =========================
    // VOLUNTEER LOGIN PAGE
    // =========================

    @GetMapping("/volunteer/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {

        if (error != null) {

            model.addAttribute(
                    "loginError",
                    "Invalid email or password."
            );
        }

        if (logout != null) {

            model.addAttribute(
                    "logoutMessage",
                    "You have been logged out successfully."
            );
        }

        return "volunteer/account-login";
    }
}