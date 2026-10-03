package com.frdms.frdms.shelter_management.service;

import com.frdms.frdms.shelter_management.entity.Shelter;
import com.frdms.frdms.shelter_management.entity.ShelterAllocation;
import com.frdms.frdms.shelter_management.repository.ShelterAllocationRepository;
import com.frdms.frdms.shelter_management.repository.ShelterRepository;
import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShelterAllocationService {

    private final ShelterAllocationRepository shelterAllocationRepository;
    private final ShelterRepository shelterRepository;
    private final AffectedFamilyInformationService
            affectedFamilyInformationService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ShelterAllocationService(
            ShelterAllocationRepository shelterAllocationRepository,
            ShelterRepository shelterRepository,
            AffectedFamilyInformationService
                    affectedFamilyInformationService
    ) {

        this.shelterAllocationRepository =
                shelterAllocationRepository;

        this.shelterRepository =
                shelterRepository;

        this.affectedFamilyInformationService =
                affectedFamilyInformationService;
    }


    // =========================================================
    // SM-US8
    // ALLOCATE AFFECTED FAMILY TO SHELTER
    // =========================================================

    @Transactional
    public ShelterAllocation allocateFamily(
            Long affectedFamilyId,
            Long shelterId
    ) {

        // -----------------------------------------------------
        // GET AFFECTED FAMILY
        // -----------------------------------------------------

        AffectedFamilyInformation affectedFamily =
                affectedFamilyInformationService
                        .getById(affectedFamilyId);


        // -----------------------------------------------------
        // CHECK WHETHER FAMILY ALREADY HAS AN ALLOCATION
        // -----------------------------------------------------

        if (shelterAllocationRepository
                .existsByAffectedFamily_Id(affectedFamilyId)) {

            throw new IllegalArgumentException(
                    "This family has already been allocated to a shelter."
            );
        }


        // -----------------------------------------------------
        // CHECK WHETHER SHELTER EXISTS
        // -----------------------------------------------------

        Shelter shelter =
                shelterRepository.findById(shelterId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Shelter not found."
                                )
                        );


        // -----------------------------------------------------
        // CHECK WHETHER SHELTER IS CLOSED
        // -----------------------------------------------------

        if (shelter.getStatus()
                == Shelter.ShelterStatus.CLOSED) {

            throw new IllegalArgumentException(
                    "The selected shelter is closed."
            );
        }


        // -----------------------------------------------------
        // CHECK WHETHER SHELTER IS FULL
        // -----------------------------------------------------

        if (shelter.getAvailableCapacity() <= 0) {

            throw new IllegalArgumentException(
                    "The selected shelter has no available capacity."
            );
        }


        // -----------------------------------------------------
        // CHECK FAMILY SIZE
        // -----------------------------------------------------

        if (affectedFamily.getTotalFamilyMembers() == null
                || affectedFamily.getTotalFamilyMembers() <= 0) {

            throw new IllegalArgumentException(
                    "Invalid family size."
            );
        }


        // -----------------------------------------------------
        // CHECK SHELTER CAPACITY
        // -----------------------------------------------------

        if (affectedFamily.getTotalFamilyMembers()
                > shelter.getAvailableCapacity()) {

            throw new IllegalArgumentException(
                    "The selected shelter does not have enough capacity for this family."
            );
        }


        // -----------------------------------------------------
        // CHECK DISASTER MATCH
        // -----------------------------------------------------

        if (affectedFamily.getVictim() == null
                || affectedFamily.getVictim().getDisaster() == null) {

            throw new IllegalArgumentException(
                    "The family's disaster information is unavailable."
            );
        }


        String familyDisaster =
                affectedFamily
                        .getVictim()
                        .getDisaster()
                        .getEventName();

        if (familyDisaster == null
                || shelter.getDisasterName() == null
                || !familyDisaster.equalsIgnoreCase(
                shelter.getDisasterName()
        )) {

            throw new IllegalArgumentException(
                    "The selected shelter is not associated with the family's disaster."
            );
        }


        // -----------------------------------------------------
        // CREATE ALLOCATION
        // -----------------------------------------------------

        ShelterAllocation allocation =
                new ShelterAllocation();

        allocation.setAffectedFamily(
                affectedFamily
        );

        allocation.setShelter(
                shelter
        );


        // -----------------------------------------------------
        // UPDATE VICTIM SHELTER
        // -----------------------------------------------------

        affectedFamily.getVictim().setShelter(shelter);


        // -----------------------------------------------------
        // UPDATE SHELTER OCCUPANCY
        // -----------------------------------------------------

        int newOccupancy =
                shelter.getCurrentOccupancy()
                        + affectedFamily.getTotalFamilyMembers();

        shelter.setCurrentOccupancy(
                newOccupancy
        );


        // -----------------------------------------------------
        // UPDATE SHELTER STATUS
        // -----------------------------------------------------

        if (newOccupancy
                >= shelter.getMaximumCapacity()) {

            shelter.setStatus(
                    Shelter.ShelterStatus.FULL
            );

        } else {

            shelter.setStatus(
                    Shelter.ShelterStatus.AVAILABLE
            );
        }


        // -----------------------------------------------------
        // SAVE SHELTER
        // -----------------------------------------------------

        shelterRepository.save(shelter);


        // -----------------------------------------------------
        // SAVE ALLOCATION
        // -----------------------------------------------------

        return shelterAllocationRepository.save(
                allocation
        );
    }


    // =========================================================
    // GET SUITABLE SHELTERS FOR A FAMILY
    // SM-US8
    // =========================================================

    @Transactional(readOnly = true)
    public java.util.List<Shelter> getSuitableShelters(
            Long affectedFamilyId
    ) {

        // -----------------------------------------------------
        // GET AFFECTED FAMILY
        // -----------------------------------------------------

        AffectedFamilyInformation affectedFamily =
                affectedFamilyInformationService
                        .getById(affectedFamilyId);


        // -----------------------------------------------------
        // CHECK FAMILY DISASTER
        // -----------------------------------------------------

        if (affectedFamily.getVictim() == null
                || affectedFamily.getVictim().getDisaster() == null) {

            throw new IllegalArgumentException(
                    "The family's disaster information is unavailable."
            );
        }


        String familyDisaster =
                affectedFamily
                        .getVictim()
                        .getDisaster()
                        .getEventName();


        // -----------------------------------------------------
        // GET AVAILABLE SHELTERS FOR THE DISASTER
        // -----------------------------------------------------

        java.util.List<Shelter> shelters =
                shelterRepository
                        .findByDisasterNameIgnoreCaseAndStatus(
                                familyDisaster,
                                Shelter.ShelterStatus.AVAILABLE
                        );


        // -----------------------------------------------------
        // FILTER BY FAMILY SIZE
        // -----------------------------------------------------

        int familySize =
                affectedFamily.getTotalFamilyMembers();


        return shelters.stream()
                .filter(shelter ->
                        shelter.getAvailableCapacity()
                                >= familySize
                )
                .toList();
    }


    // =========================================================
    // GET ALL SHELTER ALLOCATIONS
    // SM-US8
    // =========================================================

    @Transactional(readOnly = true)
    public java.util.List<ShelterAllocation> getAllAllocations() {

        return shelterAllocationRepository
                .findAllByOrderByIdDesc();
    }


    // =========================================================
    // CHECK WHETHER FAMILY IS ALREADY ALLOCATED
    // SM-US9
    // =========================================================

    @Transactional(readOnly = true)
    public boolean isFamilyAllocated(Long affectedFamilyId) {

        return shelterAllocationRepository
                .existsByAffectedFamily_Id(affectedFamilyId);
    }


    // =========================================================
    // SM-US9
    // UPDATE FAMILY ALLOCATION
    // =========================================================

    @Transactional
    public ShelterAllocation updateFamilyAllocation(
            Long affectedFamilyId,
            Long newShelterId
    ) {

        AffectedFamilyInformation affectedFamily =
                affectedFamilyInformationService.getById(
                        affectedFamilyId
                );

        ShelterAllocation allocation =
                shelterAllocationRepository
                        .findByAffectedFamily_Id(affectedFamilyId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "This family has not been allocated to a shelter."
                                )
                        );

        Shelter oldShelter =
                allocation.getShelter();

        Shelter newShelter =
                shelterRepository.findById(newShelterId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "New shelter not found."
                                )
                        );


        // -----------------------------------------------------
        // Prevent selecting the same shelter
        // -----------------------------------------------------

        if (oldShelter.getId().equals(newShelterId)) {

            throw new IllegalArgumentException(
                    "The family is already allocated to this shelter."
            );
        }


        // -----------------------------------------------------
        // Validate new shelter status
        // -----------------------------------------------------

        if (newShelter.getStatus()
                == Shelter.ShelterStatus.CLOSED) {

            throw new IllegalArgumentException(
                    "The selected shelter is closed."
            );
        }


        // -----------------------------------------------------
        // Validate family size
        // -----------------------------------------------------

        if (affectedFamily.getTotalFamilyMembers() == null
                || affectedFamily.getTotalFamilyMembers() <= 0) {

            throw new IllegalArgumentException(
                    "Invalid family size."
            );
        }

        int familySize =
                affectedFamily.getTotalFamilyMembers();


        // -----------------------------------------------------
        // Validate disaster compatibility
        // -----------------------------------------------------

        if (affectedFamily.getVictim() == null
                || affectedFamily.getVictim().getDisaster() == null) {

            throw new IllegalArgumentException(
                    "The family's disaster information is unavailable."
            );
        }

        String familyDisaster =
                affectedFamily.getVictim()
                        .getDisaster()
                        .getEventName();

        if (familyDisaster == null
                || newShelter.getDisasterName() == null
                || !familyDisaster.equalsIgnoreCase(
                newShelter.getDisasterName()
        )) {

            throw new IllegalArgumentException(
                    "The selected shelter is not associated with the family's disaster."
            );
        }


        // -----------------------------------------------------
        // Check new shelter capacity
        // -----------------------------------------------------

        if (newShelter.getAvailableCapacity()
                < familySize) {

            throw new IllegalArgumentException(
                    "The selected shelter does not have enough capacity for this family."
            );
        }


        // -----------------------------------------------------
        // Remove family from old shelter
        // -----------------------------------------------------

        int oldOccupancy =
                oldShelter.getCurrentOccupancy();

        if (oldOccupancy < familySize) {

            throw new IllegalArgumentException(
                    "The old shelter occupancy is invalid."
            );
        }

        int updatedOldOccupancy =
                oldOccupancy - familySize;

        oldShelter.setCurrentOccupancy(
                updatedOldOccupancy
        );


        // -----------------------------------------------------
        // Update old shelter status
        // -----------------------------------------------------

        if (oldShelter.getStatus()
                != Shelter.ShelterStatus.CLOSED) {

            oldShelter.setStatus(
                    Shelter.ShelterStatus.AVAILABLE
            );
        }


        // -----------------------------------------------------
        // Add family to new shelter
        // -----------------------------------------------------

        int newOccupancy =
                newShelter.getCurrentOccupancy()
                        + familySize;

        newShelter.setCurrentOccupancy(
                newOccupancy
        );


        // -----------------------------------------------------
        // Update new shelter status
        // -----------------------------------------------------

        if (newOccupancy
                >= newShelter.getMaximumCapacity()) {

            newShelter.setStatus(
                    Shelter.ShelterStatus.FULL
            );

        } else {

            newShelter.setStatus(
                    Shelter.ShelterStatus.AVAILABLE
            );
        }


        // -----------------------------------------------------
        // Save shelter changes
        // -----------------------------------------------------

        shelterRepository.save(oldShelter);
        shelterRepository.save(newShelter);


        // -----------------------------------------------------
        // Update allocation
        // -----------------------------------------------------

        allocation.setShelter(newShelter);


        // -----------------------------------------------------
        // UPDATE VICTIM SHELTER
        // -----------------------------------------------------

        affectedFamily.getVictim().setShelter(newShelter);


        // -----------------------------------------------------
        // SAVE ALLOCATION
        // -----------------------------------------------------

        return shelterAllocationRepository.save(
                allocation
        );
    }


    // =========================================================
    // SM-US9
    // GET ALLOCATION BY FAMILY ID
    // =========================================================

    @Transactional(readOnly = true)
    public ShelterAllocation getAllocationByFamilyId(
            Long affectedFamilyId
    ) {

        return shelterAllocationRepository
                .findByAffectedFamily_Id(affectedFamilyId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "This family has not been allocated to a shelter."
                        )
                );
    }
}