package com.frdms.frdms.shelter_management.service;

import com.frdms.frdms.shelter_management.entity.Shelter;
import com.frdms.frdms.shelter_management.repository.ShelterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShelterService {

    private final ShelterRepository shelterRepository;

    public ShelterService(
            ShelterRepository shelterRepository
    ) {
        this.shelterRepository = shelterRepository;
    }

    // =========================
    // SM-US1
    // REGISTER SHELTER
    // =========================

    public Shelter registerShelter(Shelter shelter) {

        if (shelterRepository.existsByLocationIgnoreCase(
                shelter.getLocation()
        )) {

            throw new IllegalArgumentException(
                    "A shelter already exists at this location."
            );
        }

        if (shelter.getCurrentOccupancy() == null) {
            shelter.setCurrentOccupancy(0);
        }

        if (shelter.getCurrentOccupancy() > shelter.getMaximumCapacity()) {
            throw new IllegalArgumentException(
                    "Current occupancy cannot be greater than maximum capacity."
            );
        }

        if (shelter.getCurrentOccupancy()
                .equals(shelter.getMaximumCapacity())) {

            shelter.setStatus(
                    Shelter.ShelterStatus.FULL
            );

        } else {

            if (shelter.getStatus() == Shelter.ShelterStatus.FULL) {
                throw new IllegalArgumentException(
                        "Shelter status cannot be FULL while available capacity remains."
                );
            }

            if (shelter.getStatus() == null) {
                shelter.setStatus(
                        Shelter.ShelterStatus.AVAILABLE
                );
            }
        }

        return shelterRepository.save(shelter);
    }


    // =========================
    // VIEW ONE SHELTER BY ID
    // =========================

    public Shelter getShelterById(Long id) {

        return shelterRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Shelter not found."
                        )
                );
    }


    // =========================
    // SM-US4
    // VIEW ALL SHELTERS
    // =========================

    public List<Shelter> getAllShelters() {

        return shelterRepository.findAllByOrderByIdDesc();
    }


    // =========================
    // SM-US5 / SM-US10
    // UPDATE SHELTER DETAILS
    // Contact Person
    // Contact Number
    // Disaster Name
    // Current Occupancy
    // Status
    // =========================

    public Shelter updateShelterDetails(
            Long shelterId,
            String contactPerson,
            String contactNumber,
            String disasterName,
            Integer currentOccupancy,
            Shelter.ShelterStatus status
    ) {

        Shelter shelter =
                shelterRepository.findById(shelterId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Shelter not found."
                                )
                        );

        if (contactPerson == null || contactPerson.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Contact person cannot be empty."
            );
        }

        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Contact number cannot be empty."
            );
        }

        if (!contactNumber.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException(
                    "Contact number must be a valid 10-digit number starting with 0."
            );
        }

        if (disasterName == null || disasterName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Disaster name is required."
            );
        }

        if (currentOccupancy == null) {
            throw new IllegalArgumentException(
                    "Current occupancy is required."
            );
        }

        if (currentOccupancy < 0) {
            throw new IllegalArgumentException(
                    "Current occupancy cannot be negative."
            );
        }

        if (currentOccupancy > shelter.getMaximumCapacity()) {
            throw new IllegalArgumentException(
                    "Current occupancy cannot be greater than maximum capacity."
            );
        }

        shelter.setContactPerson(contactPerson);
        shelter.setContactNumber(contactNumber);
        shelter.setDisasterName(disasterName);
        shelter.setCurrentOccupancy(currentOccupancy);

        if (currentOccupancy.equals(shelter.getMaximumCapacity())) {

            shelter.setStatus(
                    Shelter.ShelterStatus.FULL
            );

        } else {

            if (status == Shelter.ShelterStatus.FULL) {
                throw new IllegalArgumentException(
                        "Shelter status cannot be FULL while available capacity remains."
                );
            }

            shelter.setStatus(status);
        }

        return shelterRepository.save(shelter);
    }


    // =========================
    // DELETE SHELTER
    // =========================

    public void deleteShelter(Long shelterId) {

        Shelter shelter =
                shelterRepository.findById(shelterId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Shelter not found."
                                )
                        );

        if (shelter.getCurrentOccupancy() > 0) {
            throw new IllegalArgumentException(
                    "Shelter cannot be deleted while people are currently staying there."
            );
        }

        shelterRepository.delete(shelter);
    }
}
