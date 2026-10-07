package com.frdms.frdms.distribution_management.service;

import com.frdms.frdms.distribution_management.entity.Driver;
import com.frdms.frdms.distribution_management.entity.TransportAssignment;
import com.frdms.frdms.distribution_management.entity.TransportResource;
import com.frdms.frdms.distribution_management.repository.TransportAssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransportAssignmentService {

    private final TransportAssignmentRepository assignmentRepository;
    private final TransportResourceService transportResourceService;
    private final DriverService driverService;

    public TransportAssignmentService(
            TransportAssignmentRepository assignmentRepository,
            TransportResourceService transportResourceService,
            DriverService driverService) {

        this.assignmentRepository = assignmentRepository;
        this.transportResourceService = transportResourceService;
        this.driverService = driverService;
    }

    public TransportAssignment saveAssignment(
            Long packageId,
            Long vehicleId,
            Long driverId) {

        // Find the selected vehicle
        TransportResource vehicle =
                transportResourceService.getTransportResourceById(vehicleId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid vehicle ID: " + vehicleId));

        // Check whether the vehicle is already assigned
        if (!isVehicleAvailable(vehicleId)) {
            throw new IllegalStateException(
                    "This vehicle is already assigned to another active distribution.");
        }

        /*
         * CASE 1:
         * Vehicle has an owner-provided driver.
         *
         * We do not use an independent driver.
         * The driverId remains null because the owner-provided
         * driver is stored inside the vehicle record.
         */
        if (Boolean.TRUE.equals(vehicle.getHasDriver())) {

            driverId = null;
        }

        /*
         * CASE 2:
         * Vehicle has NO driver.
         *
         * An independent driver must be selected.
         */
        else {

            if (driverId == null) {
                throw new IllegalStateException(
                        "Please select an available independent driver.");
            }

            /// Check that the selected driver actually exists
            Long selectedDriverId = driverId;

            Driver driver =
                    driverService.getDriverById(selectedDriverId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Invalid driver ID: " + selectedDriverId));
            // Check whether the independent driver is already assigned
            if (!isDriverAvailable(driver.getId())) {
                throw new IllegalStateException(
                        "This driver is already assigned to another active distribution.");
            }
        }

        // Create the transport assignment
        TransportAssignment assignment =
                new TransportAssignment();

        assignment.setPackageId(packageId);
        assignment.setVehicleId(vehicleId);
        assignment.setDriverId(driverId);
        assignment.setStatus("ACTIVE");
        assignment.setAssignedAt(LocalDateTime.now());

        return assignmentRepository.save(assignment);
    }

    public List<TransportAssignment> getActiveAssignments() {
        return assignmentRepository.findByStatus("ACTIVE");
    }

    public boolean isVehicleAvailable(Long vehicleId) {
        return !assignmentRepository
                .existsByVehicleIdAndStatus(
                        vehicleId,
                        "ACTIVE"
                );
    }

    public boolean isDriverAvailable(Long driverId) {
        return !assignmentRepository
                .existsByDriverIdAndStatus(
                        driverId,
                        "ACTIVE"
                );
    }

    public void completeAssignment(Long assignmentId) {

        TransportAssignment assignment =
                assignmentRepository.findById(assignmentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid assignment ID: "
                                                + assignmentId));

        assignment.setStatus("COMPLETED");
        assignment.setCompletedAt(LocalDateTime.now());

        assignmentRepository.save(assignment);
    }
    public void completeAssignmentByPackageId(Long packageId) {

        List<TransportAssignment> assignments =
                assignmentRepository.findByPackageId(packageId);

        for (TransportAssignment assignment : assignments) {

            if ("ACTIVE".equals(assignment.getStatus())) {
                assignment.setStatus("COMPLETED");
                assignment.setCompletedAt(LocalDateTime.now());

                assignmentRepository.save(assignment);
            }
        }
    }
}