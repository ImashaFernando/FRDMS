package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.entity.Driver;
import com.frdms.frdms.distribution_management.entity.TransportResource;
import com.frdms.frdms.distribution_management.service.DriverService;
import com.frdms.frdms.distribution_management.service.TransportAssignmentService;
import com.frdms.frdms.distribution_management.service.TransportResourceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/distribution/transport/availability")
public class TransportAvailabilityController {

    private final TransportResourceService transportResourceService;
    private final DriverService driverService;
    private final TransportAssignmentService assignmentService;

    public TransportAvailabilityController(
            TransportResourceService transportResourceService,
            DriverService driverService,
            TransportAssignmentService assignmentService) {

        this.transportResourceService = transportResourceService;
        this.driverService = driverService;
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public String showAvailability(Model model) {

        List<TransportResource> allVehicles =
                transportResourceService.getAllTransportResources();

        List<Driver> allDrivers =
                driverService.getAllDrivers();

        List<TransportResource> availableVehicles =
                allVehicles.stream()
                        .filter(vehicle ->
                                assignmentService.isVehicleAvailable(
                                        vehicle.getId()))
                        .collect(Collectors.toList());

        List<Driver> availableDrivers =
                allDrivers.stream()
                        .filter(driver ->
                                assignmentService.isDriverAvailable(
                                        driver.getId()))
                        .collect(Collectors.toList());

        model.addAttribute(
                "availableVehicles",
                availableVehicles
        );

        model.addAttribute(
                "availableDrivers",
                availableDrivers
        );

        return "distribution/transport-availability";
    }
}