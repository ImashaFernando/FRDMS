package com.frdms.frdms.volunteer_management.controller;

import com.frdms.frdms.volunteer_management.entity.VolunteerProfile;
import com.frdms.frdms.volunteer_management.service.VolunteerProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class VolunteerProfileController {

    private final VolunteerProfileService volunteerProfileService;

    public VolunteerProfileController(
            VolunteerProfileService volunteerProfileService
    ) {
        this.volunteerProfileService = volunteerProfileService;
    }


    // =========================
    // SHOW VOLUNTEER PROFILE
    // =========================

    @GetMapping("/volunteer/profile")
    public String showVolunteerProfile(
            Authentication authentication,
            Model model
    ) {

        String email = authentication.getName();

        Optional<VolunteerProfile> existingProfile =
                volunteerProfileService.getProfileByVolunteerEmail(email);

        if (existingProfile.isPresent()) {

            model.addAttribute(
                    "volunteerProfile",
                    existingProfile.get()
            );

            return "volunteer/profile-view";
        }

        model.addAttribute(
                "volunteerProfile",
                new VolunteerProfile()
        );

        return "volunteer/profile-form";
    }


    // =========================
    // SUBMIT VOLUNTEER PROFILE
    // =========================

    @PostMapping("/volunteer/profile")
    public String submitVolunteerProfile(
            @Valid VolunteerProfile volunteerProfile,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            return "volunteer/profile-form";
        }

        String email = authentication.getName();

        try {

            volunteerProfileService.createProfile(
                    email,
                    volunteerProfile
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Volunteer profile submitted successfully."
            );

            return "redirect:/volunteer/profile";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "profileError",
                    exception.getMessage()
            );

            return "volunteer/profile-form";
        }
    }


    // =========================
    // SHOW EDIT PROFILE FORM
    // ONLY PENDING REGISTRATIONS
    // =========================

    @GetMapping("/volunteer/profile/edit")
    public String showEditProfileForm(
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        String email = authentication.getName();

        Optional<VolunteerProfile> existingProfile =
                volunteerProfileService.getProfileByVolunteerEmail(email);

        if (existingProfile.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Volunteer profile not found."
            );

            return "redirect:/volunteer/profile";
        }

        VolunteerProfile volunteerProfile =
                existingProfile.get();

        if (volunteerProfile.getRegistrationStatus()
                != VolunteerProfile.RegistrationStatus.PENDING) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Volunteer registration can only be edited while it is pending."
            );

            return "redirect:/volunteer/profile";
        }

        model.addAttribute(
                "volunteerProfile",
                volunteerProfile
        );

        return "volunteer/profile-edit";
    }


    // =========================
    // UPDATE VOLUNTEER PROFILE
    // ONLY PENDING REGISTRATIONS
    // =========================

    @PostMapping("/volunteer/profile/update")
    public String updateVolunteerProfile(
            @Valid VolunteerProfile volunteerProfile,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            return "volunteer/profile-edit";
        }

        String email = authentication.getName();

        try {

            volunteerProfileService.updatePendingProfile(
                    email,
                    volunteerProfile
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Volunteer registration updated successfully."
            );

            return "redirect:/volunteer/profile";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "profileError",
                    exception.getMessage()
            );

            return "volunteer/profile-edit";
        }
    }


    // =========================
    // WITHDRAW VOLUNTEER PROFILE
    // ONLY PENDING REGISTRATIONS
    // =========================

    @PostMapping("/volunteer/profile/withdraw")
    public String withdrawVolunteerProfile(
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {

        String email = authentication.getName();

        try {

            volunteerProfileService.withdrawPendingProfile(
                    email
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Volunteer registration withdrawn successfully."
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/volunteer/profile";
    }
}
