package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.entity.Driver;
import com.frdms.frdms.distribution_management.entity.TransportResource;
import com.frdms.frdms.distribution_management.service.DriverService;
import com.frdms.frdms.distribution_management.service.ReliefPackageService;
import com.frdms.frdms.distribution_management.service.TransportAssignmentService;
import com.frdms.frdms.distribution_management.service.TransportResourceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/distribution/transport/assign")
public class TransportAssignmentController {

    private final TransportResourceService transportResourceService;
    private final DriverService driverService;
    private final TransportAssignmentService assignmentService;
    private final ReliefPackageService packageService;

    public TransportAssignmentController(
            TransportResourceService transportResourceService,
            DriverService driverService,
            TransportAssignmentService assignmentService,
            ReliefPackageService packageService) {

        this.transportResourceService = transportResourceService;
        this.driverService = driverService;
        this.assignmentService = assignmentService;
        this.packageService = packageService;
    }

    @GetMapping("/{packageId}")
    public String showAssignmentPage(
            @PathVariable Long packageId,
            Model model) {

        List<TransportResource> availableVehicles =
                transportResourceService
                        .getAllTransportResources()
                        .stream()
                        .filter(vehicle ->
                                assignmentService.isVehicleAvailable(
                                        vehicle.getId()))
                        .collect(Collectors.toList());

        List<Driver> availableDrivers =
                driverService
                        .getAllDrivers()
                        .stream()
                        .filter(driver ->
                                assignmentService.isDriverAvailable(
                                        driver.getId()))
                        .collect(Collectors.toList());

        model.addAttribute("packageId", packageId);
        model.addAttribute("availableVehicles", availableVehicles);
        model.addAttribute("availableDrivers", availableDrivers);

        return "distribution/assign-transport";
    }

    @PostMapping("/save")
    public String saveAssignment(
            @RequestParam Long packageId,
            @RequestParam Long vehicleId,
            @RequestParam(required = false) Long driverId) {

        // Get the selected vehicle
        TransportResource vehicle =
                transportResourceService
                        .getTransportResourceById(vehicleId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid vehicle ID: " + vehicleId));

        // Get the package
        var reliefPackage =
                packageService.getPackageById(packageId);
        // Determine the driver name
        String assignedDriverName;

        if (Boolean.TRUE.equals(vehicle.getHasDriver())) {

            // Vehicle already has a driver provided by the owner
            assignedDriverName = vehicle.getDriverName();

        } else {

            // Vehicle has no driver, so an independent driver is required
            if (driverId == null) {
                throw new IllegalStateException(
                        "Please select an available independent driver.");
            }

            Driver driver =
                    driverService
                            .getDriverById(driverId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Invalid driver ID: " + driverId));

            assignedDriverName = driver.getDriverName();
        }

        // Save transport assignment
        assignmentService.saveAssignment(
                packageId,
                vehicleId,
                driverId
        );

        // Update the existing package fields
        reliefPackage.setVehicleNumber(vehicle.getVehicleNumber());
        reliefPackage.setDriverName(assignedDriverName);

        packageService.savePackage(reliefPackage);

        // Go directly to Package Details
        return "redirect:/distribution/packages/" + packageId;
    }
}