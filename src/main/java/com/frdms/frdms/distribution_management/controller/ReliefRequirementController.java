package com.frdms.distribution_management.controller;

import com.frdms.distribution_management.entity.ReliefRequirement;
import com.frdms.distribution_management.service.ReliefRequirementService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReliefRequirementController {

    private final ReliefRequirementService service;

    public ReliefRequirementController(ReliefRequirementService service) {
        this.service = service;
    }

    @GetMapping("/distribution/requirements/new")
    public String showRequirementForm(Model model) {

        model.addAttribute("reliefRequirement", new ReliefRequirement());

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
}
