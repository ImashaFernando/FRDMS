package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.entity.ReliefPackage;
import com.frdms.frdms.distribution_management.service.ReliefPackageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/distribution/packages")
public class ReliefPackageController {

    private final ReliefPackageService packageService;

    public ReliefPackageController(ReliefPackageService packageService) {
        this.packageService = packageService;
    }


    // Show Packages page
    @GetMapping
    public String showPackages(Model model) {

        var packages = packageService.getAllPackages();

        model.addAttribute("packages", packages);

        long successfulCount = packages.stream()
                .filter(p -> p.getStatus().name().equals("SUCCESSFUL"))
                .count();

        long requestingItemsCount = packages.stream()
                .filter(p -> p.getStatus().name().equals("REQUESTING_ITEMS"))
                .count();

        model.addAttribute("successfulCount", successfulCount);
        model.addAttribute("requestingItemsCount", requestingItemsCount);

        return "distribution/packages";
    }


    // Show Create Package page
    @GetMapping("/new")
    public String showCreatePackageForm(Model model) {

        ReliefPackage reliefPackage = new ReliefPackage();

        // Start with one empty relief item row
        reliefPackage.addItem(new com.frdms.frdms.distribution_management.entity.ReliefPackageItem());

        model.addAttribute("reliefPackage", reliefPackage);

        return "distribution/create-package";
    }


    // Save Package
    @PostMapping("/save")
    public String savePackage(
            @Valid @ModelAttribute("reliefPackage")
            ReliefPackage reliefPackage,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "distribution/create-package";
        }

        packageService.savePackage(reliefPackage);

        return "redirect:/distribution/packages";
    }


    // Show Package Details
    @GetMapping("/{id}")
    public String showPackageDetails(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "reliefPackage",
                packageService.getPackageById(id)
        );

        return "distribution/package-details";
    }
    @GetMapping("/{id}/edit")
    public String showEditPackageForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "reliefPackage",
                packageService.getPackageById(id)
        );

        return "distribution/edit-package";
    }
    @PostMapping("/update")
    public String updatePackage(
            @Valid @ModelAttribute("reliefPackage")
            ReliefPackage reliefPackage,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "distribution/edit-package";
        }

        packageService.savePackage(reliefPackage);

        return "redirect:/distribution/packages";
    }
    // Cancel Package
    @PostMapping("/{id}/cancel")
    public String cancelPackage(@PathVariable Long id) {

        packageService.cancelPackage(id);

        return "redirect:/distribution/packages";
    }
    // Recycle Bin
    @GetMapping("/recycle-bin")
    public String showRecycleBin(Model model) {

        model.addAttribute(
                "cancelledPackages",
                packageService.getCancelledPackages()
        );

        return "distribution/recycle-bin";
    }
    // Restore Package
    @PostMapping("/{id}/restore")
    public String restorePackage(@PathVariable Long id) {

        packageService.restorePackage(id);

        return "redirect:/distribution/packages";
    }
    // Permanently Delete Package
    @PostMapping("/{id}/delete")
    public String permanentlyDeletePackage(@PathVariable Long id) {

        packageService.permanentlyDeletePackage(id);

        return "redirect:/distribution/packages/recycle-bin";
    }
}