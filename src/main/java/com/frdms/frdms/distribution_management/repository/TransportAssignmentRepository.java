package com.frdms.frdms.distribution_management.repository;

import com.frdms.frdms.distribution_management.entity.TransportAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransportAssignmentRepository
        extends JpaRepository<TransportAssignment, Long> {

    List<TransportAssignment> findByStatus(String status);

    boolean existsByVehicleIdAndStatus(Long vehicleId, String status);

    boolean existsByDriverIdAndStatus(Long driverId, String status);
}
