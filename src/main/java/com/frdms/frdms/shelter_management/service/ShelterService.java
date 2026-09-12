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

        return shelterRepository.findAll();
    }


    // =========================
    // SM-US10
    // UPDATE SHELTER STATUS
    // =========================

    public Shelter updateShelterStatus(
            Long shelterId,
            Shelter.ShelterStatus newStatus
    ) {

        Shelter shelter =
                shelterRepository.findById(shelterId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Shelter not found."
                                )
                        );

        if (shelter.getCurrentOccupancy()
                .equals(shelter.getMaximumCapacity())) {

            shelter.setStatus(
                    Shelter.ShelterStatus.FULL
            );

        } else {

            shelter.setStatus(newStatus);
        }

        return shelterRepository.save(shelter);
    }
}