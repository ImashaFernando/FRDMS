package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.entity.ReliefPackage;
import com.frdms.frdms.distribution_management.service.ReliefPackageService;
import com.frdms.frdms.distribution_management.service.TransportAssignmentService;
import com.frdms.frdms.distribution_management.service.ReliefRequirementService;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.frdms.frdms.distribution_management.entity.PackageStatus;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/distribution/packages")
public class ReliefPackageController {

    private final ReliefPackageService packageService;

    private final ReliefRequirementService reliefRequirementService;

    private final TransportAssignmentService transportAssignmentService;
    public ReliefPackageController(
            ReliefPackageService packageService,
            ReliefRequirementService reliefRequirementService,
            TransportAssignmentService transportAssignmentService) {
        this.packageService = packageService;
        this.reliefRequirementService = reliefRequirementService;
        this.transportAssignmentService = transportAssignmentService;
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
    public String showCreatePackageForm(
            @RequestParam Long requirementId,
            Model model) {

        // Get the exact requirement selected by the Distribution officer
        ReliefRequirement requirement =
                reliefRequirementService.getRequirementById(requirementId);

        if (packageService.existsByRequirementId(requirementId)) {
            return "redirect:/distribution/requirements/"
                    + requirementId
                    + "?packageExists=true";
        }

        // Only approved requirements can be used to create a package
        if (requirement.getStatus() == null
                || !"APPROVED".equalsIgnoreCase(
                requirement.getStatus().trim())) {

            return "redirect:/distribution/requirements";
        }

        // Create a new relief package
        ReliefPackage reliefPackage = new ReliefPackage();
        reliefPackage.setRequirementId(requirementId);

        // =========================================================
        // AUTOMATICALLY FILL REQUIREMENT / VICTIM DETAILS
        // =========================================================

        // Recipient name comes from the approved requirement
        reliefPackage.setRecipientName(
                requirement.getRecipientName()
        );

        // Affected area comes from the approved requirement
        reliefPackage.setAffectedArea(
                requirement.getAffectedArea()
        );

        if (requirement.getVictim() != null
                && requirement.getVictim().getDisaster() != null) {

            reliefPackage.setDisasterName(
                    requirement.getVictim()
                            .getDisaster()
                            .getEventName()
            );
        }

        // Shelter comes from the approved requirement
        reliefPackage.setShelter(
                requirement.getShelter()
        );

        // Contact number comes from the victim
        if (requirement.getVictim() != null) {

            reliefPackage.setContactNumber(
                    requirement.getVictim().getPhoneNumber()
            );
        }

        // =========================================================
        // AUTOMATIC DELIVERY ADDRESS
        // =========================================================

        if (requirement.getShelter() != null
                && !requirement.getShelter().isBlank()
                && !"Not Assigned".equalsIgnoreCase(
                requirement.getShelter().trim())) {

            // Victim has an assigned shelter
            reliefPackage.setDeliveryAddress(
                    requirement.getShelter()
            );

        } else if (requirement.getVictim() != null) {

            // No shelter → use victim's registered address
            reliefPackage.setDeliveryAddress(
                    requirement.getVictim().getAddress()
            );
        }

        // Start with one empty relief item row
        reliefPackage.addItem(
                new com.frdms.frdms.distribution_management.entity.ReliefPackageItem()
        );

        model.addAttribute(
                "reliefPackage",
                reliefPackage
        );

        model.addAttribute(
                "requirement",
                requirement
        );

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

        try {
            packageService.savePackage(reliefPackage);
        } catch (IllegalStateException e) {
            return "redirect:/distribution/requirements/"
                    + reliefPackage.getRequirementId()
                    + "?packageExists=true";
        }

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

        if (reliefPackage.getStatus() == PackageStatus.SUCCESSFUL
                && reliefPackage.getDistributionCompletedAt() == null) {

            reliefPackage.setDistributionCompletedAt(LocalDateTime.now());

            // Release the assigned vehicle and driver
            transportAssignmentService.completeAssignmentByPackageId(
                    reliefPackage.getId()
            );
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