package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.entity.TransportResource;
import com.frdms.frdms.distribution_management.service.TransportResourceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/distribution/transport")
public class TransportResourceController {

    private final TransportResourceService transportResourceService;

    public TransportResourceController(
            TransportResourceService transportResourceService) {
        this.transportResourceService = transportResourceService;
    }

    // View all registered vehicles
    @GetMapping
    public String showTransportResources(Model model) {

        model.addAttribute(
                "transportResources",
                transportResourceService.getAllTransportResources()
        );

        return "distribution/transport";
    }

    // Show vehicle registration form
    @GetMapping("/new")
    public String showRegistrationForm(Model model) {

        model.addAttribute(
                "transportResource",
                new TransportResource()
        );

        return "distribution/register-vehicle";
    }

    // Save registered vehicle
    @PostMapping("/save")
    public String saveTransportResource(
            @Valid @ModelAttribute("transportResource")
            TransportResource transportResource,
            BindingResult bindingResult) {

        // Driver name is required when the owner provides a driver
        if (Boolean.TRUE.equals(transportResource.getHasDriver())
                && (transportResource.getDriverName() == null
                || transportResource.getDriverName().trim().isEmpty())) {

            bindingResult.rejectValue(
                    "driverName",
                    "error.driverName",
                    "Driver name is required when the owner provides a driver"
            );
        }

        if (bindingResult.hasErrors()) {
            return "distribution/register-vehicle";
        }

        transportResourceService.saveTransportResource(transportResource);

        return "redirect:/distribution/transport";
    }


    // =========================================================
    // EDIT VEHICLE
    // =========================================================

    // Show edit form for an existing vehicle
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        TransportResource transportResource =
                transportResourceService
                        .getTransportResourceById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid vehicle ID: " + id
                                )
                        );

        model.addAttribute(
                "transportResource",
                transportResource
        );

        return "distribution/edit-vehicle";
    }


    // =========================================================
    // UPDATE VEHICLE
    // =========================================================

    // Save changes made to an existing vehicle
    @PostMapping("/update/{id}")
    public String updateTransportResource(
            @PathVariable Long id,
            @Valid @ModelAttribute("transportResource")
            TransportResource transportResource,
            BindingResult bindingResult) {

        // Driver name is required when the owner provides a driver
        if (Boolean.TRUE.equals(transportResource.getHasDriver())
                && (transportResource.getDriverName() == null
                || transportResource.getDriverName().trim().isEmpty())) {

            bindingResult.rejectValue(
                    "driverName",
                    "error.driverName",
                    "Driver name is required when the owner provides a driver"
            );
        }

        if (bindingResult.hasErrors()) {
            return "distribution/edit-vehicle";
        }

        // Make sure the existing ID is preserved
        transportResource.setId(id);

        transportResourceService.saveTransportResource(
                transportResource
        );

        return "redirect:/distribution/transport";
    }


    // =========================================================
    // DELETE VEHICLE
    // =========================================================

    // Delete a registered vehicle
    @GetMapping("/delete/{id}")
    public String deleteTransportResource(
            @PathVariable Long id) {

        transportResourceService.deleteTransportResource(id);

        return "redirect:/distribution/transport";
    }


    // =========================================================
    // TRANSPORTATION MANAGEMENT
    // =========================================================

    @GetMapping("/management")
    public String showTransportationManagement() {

        return "distribution/transportation";
    }
}