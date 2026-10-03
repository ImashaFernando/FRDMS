package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.service.ReliefRequirementService;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/distribution/requirements")
public class DistributionReliefRequirementController {

    private final ReliefRequirementService reliefRequirementService;

    public DistributionReliefRequirementController(
            ReliefRequirementService reliefRequirementService) {
        this.reliefRequirementService = reliefRequirementService;
    }

    /**
     * Display approved relief requirements received
     * from the Relief Coordinator.
     */
    @GetMapping
    public String showReceivedRequirements(Model model) {

        List<ReliefRequirement> requirements =
                reliefRequirementService.getApprovedRequirements();

        model.addAttribute("requirements", requirements);

        return "distribution/requirements";
    }

    /**
     * Display the complete details of one
     * relief requirement.
     */
    @GetMapping("/{id}")
    public String showRequirementDetails(
            @PathVariable Long id,
            Model model) {

        ReliefRequirement requirement =
                reliefRequirementService.getRequirementById(id);

        model.addAttribute("requirement", requirement);

        return "distribution/requirement-details";
    }
}