package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.entity.ReliefRequirement;
import com.frdms.frdms.distribution_management.entity.ReliefRequirementItem;
import com.frdms.frdms.distribution_management.service.ReliefRequirementService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReliefRequirementController {

    private final ReliefRequirementService service;

    public ReliefRequirementController(ReliefRequirementService service) {
        this.service = service;
    }

    @GetMapping("/distribution/requirements/new")
    public String showRequirementForm(Model model) {

        ReliefRequirement requirement = new ReliefRequirement();

        // Add one empty item row when the form first opens
        requirement.addItem(new ReliefRequirementItem());

        model.addAttribute("reliefRequirement", requirement);

        return "distribution/receive-requirement";
    }

    @PostMapping("/distribution/requirements/save")
    public String saveRequirement(
            @Valid @ModelAttribute("reliefRequirement")
            ReliefRequirement reliefRequirement,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "distribution/receive-requirement";
        }

        service.saveRequirement(reliefRequirement);

        return "redirect:/distribution/requirements";
    }

    @GetMapping("/distribution/requirements")
    public String viewRequirements(Model model) {

        model.addAttribute(
                "requirements",
                service.getAllRequirements()
        );

        return "distribution/requirements";
    }
    @GetMapping("/distribution/necessary-items")
    public String showNecessaryItems(Model model) {

        model.addAttribute(
                "necessaryItems",
                service.getNecessaryItemSummary()
        );

        return "distribution/necessary-items";
    }
    @PostMapping("/distribution/necessary-items/calculate")
    public String calculateNecessaryItems() {

        return "redirect:/distribution/necessary-items";
    }

}

