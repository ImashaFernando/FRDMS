package com.frdms.frdms.victim_management.repository;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.entity.Victim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffectedFamilyInformationRepository
        extends JpaRepository<AffectedFamilyInformation, Long> {


    // =========================================================
    // FIND FAMILY INFORMATION BY VICTIM
    // =========================================================

    Optional<AffectedFamilyInformation> findByVictim(
            Victim victim
    );


    // =========================================================
    // FIND FAMILY INFORMATION BY VICTIM ID
    // =========================================================

    Optional<AffectedFamilyInformation> findByVictim_Id(
            Long victimId
    );


    // =========================================================
    // FIND FAMILY INFORMATION FOR A DISASTER
    // =========================================================

    List<AffectedFamilyInformation>
    findByVictim_Disaster_Id(Long disasterId);


    // =========================================================
    // COUNT AFFECTED FAMILIES FOR A DISASTER
    // =========================================================

    long countByVictim_Disaster_Id(
            Long disasterId
    );


    // =========================================================
    // COUNT FAMILIES REQUIRING SHELTER
    // =========================================================

    long countByVictim_Disaster_IdAndShelterRequired(
            Long disasterId,
            Boolean shelterRequired
    );


    // =========================================================
    // FIND FAMILIES REQUIRING SHELTER
    // =========================================================

    List<AffectedFamilyInformation>
    findByShelterRequiredTrueOrderByIdDesc();
}