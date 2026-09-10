package com.frdms.frdms.disaster_management.service;

import com.frdms.frdms.disaster_management.entity.Disaster;
import com.frdms.frdms.disaster_management.repository.DisasterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisasterService {

    private final DisasterRepository disasterRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DisasterService(
            DisasterRepository disasterRepository) {

        this.disasterRepository = disasterRepository;
    }


    // =========================================================
    // CREATE NEW FLOOD EVENT
    // =========================================================

    public Disaster createDisaster(
            Disaster disaster) {

        return disasterRepository.save(disaster);
    }


    // =========================================================
    // GET ALL FLOOD EVENTS
    // =========================================================

    public List<Disaster> getAllDisasters() {

        return disasterRepository.findAll();
    }


    // =========================================================
    // FIND FLOOD EVENT BY ID
    // =========================================================

    public Disaster getDisasterById(
            Long id) {

        return disasterRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Flood event not found with ID: "
                                        + id
                        )
                );
    }


    // =========================================================
    // UPDATE EXISTING FLOOD EVENT
    // =========================================================

    public Disaster updateDisaster(
            Long id,
            Disaster updatedDisaster) {

        // Find existing flood event
        Disaster existingDisaster =
                getDisasterById(id);


        // =====================================================
        // UPDATE EVENT NAME
        // =====================================================

        existingDisaster.setEventName(
                updatedDisaster.getEventName()
        );


        // =====================================================
        // UPDATE EVENT DATE
        // =====================================================

        if (updatedDisaster.getEventDate() != null) {

            existingDisaster.setEventDate(
                    updatedDisaster.getEventDate()
            );
        }


        // =====================================================
        // UPDATE DESCRIPTION
        // =====================================================

        existingDisaster.setDescription(
                updatedDisaster.getDescription()
        );


        // =====================================================
        // SAVE UPDATED FLOOD EVENT
        // =====================================================

        return disasterRepository.save(
                existingDisaster
        );
    }
}