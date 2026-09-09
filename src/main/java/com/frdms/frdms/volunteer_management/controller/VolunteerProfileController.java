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

import java.util.Optional;

@Controller
public class VolunteerProfileController {

    private final VolunteerProfileService volunteerProfileService;

    public VolunteerProfileController(
            VolunteerProfileService volunteerProfileService
    ) {
        this.volunteerProfileService = volunteerProfileService;
    }

    @GetMapping("/volunteer/profile")
    public String showVolunteerProfileForm(
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

    @PostMapping("/volunteer/profile")
    public String submitVolunteerProfile(
            @Valid VolunteerProfile volunteerProfile,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
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

            return "redirect:/volunteer/profile-success";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "profileError",
                    exception.getMessage()
            );

            return "volunteer/profile-form";
        }
    }

    @GetMapping("/volunteer/profile-success")
    public String showProfileSuccessPage() {

        return "volunteer/profile-success";
    }
}
