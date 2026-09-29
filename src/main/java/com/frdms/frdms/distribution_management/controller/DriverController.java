package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.entity.Driver;
import com.frdms.frdms.distribution_management.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/distribution/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // View all registered independent drivers
    @GetMapping
    public String showDrivers(Model model) {
        model.addAttribute("drivers", driverService.getAllDrivers());

        return "distribution/drivers";
    }

    // Show driver registration form
    @GetMapping("/new")
    public String showRegistrationForm(Model model) {
        model.addAttribute("driver", new Driver());

        return "distribution/register-driver";
    }

    // Save independent driver
    @PostMapping("/save")
    public String saveDriver(
            @Valid @ModelAttribute("driver") Driver driver,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "distribution/register-driver";
        }

        driverService.saveDriver(driver);

        return "redirect:/distribution/drivers";
    }
    // Show edit driver form
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        Driver driver = driverService.getDriverById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid driver ID: " + id
                ));

        model.addAttribute("driver", driver);

        return "distribution/edit-driver";
    }
    // Update driver
    @PostMapping("/update")
    public String updateDriver(
            @Valid @ModelAttribute("driver") Driver driver,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "distribution/edit-driver";
        }

        driverService.saveDriver(driver);

        return "redirect:/distribution/drivers";
    }
    // Delete driver
    @PostMapping("/delete/{id}")
    public String deleteDriver(@PathVariable Long id) {

        driverService.deleteDriver(id);

        return "redirect:/distribution/drivers";
    }
}
