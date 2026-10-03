package com.frdms.frdms.shelter_management.repository;

import com.frdms.frdms.shelter_management.entity.ShelterAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ShelterAllocationRepository
        extends JpaRepository<ShelterAllocation, Long> {

    Optional<ShelterAllocation>
    findByAffectedFamily_Id(Long affectedFamilyId);

    Optional<ShelterAllocation>
    findByAffectedFamily_Victim_Id(Long victimId);

    boolean existsByAffectedFamily_Id(Long affectedFamilyId);

    List<ShelterAllocation>
    findAllByOrderByIdDesc();
}