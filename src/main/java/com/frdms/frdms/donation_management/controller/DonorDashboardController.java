package com.frdms.frdms.donation_management.controller;

import com.frdms.frdms.donation_management.entity.Donor;
import com.frdms.frdms.donation_management.repository.DonorRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DonorDashboardController {

    private final DonorRepository donorRepository;

    public DonorDashboardController(
            DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }
    @GetMapping("/donor/dashboard")
    public String donorDashboard(
            Authentication authentication,
            Model model) {

        String username = authentication.getName();

        Donor donor = donorRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Logged-in donor not found"
                        ));

        model.addAttribute("donor", donor);

        return "donor/donor-dashboard";
    }

}
