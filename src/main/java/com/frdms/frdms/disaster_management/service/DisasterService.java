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

        return disasterRepository.findByArchivedFalse();
    }


    // =========================================================
    // SEARCH FLOOD EVENTS
    // =========================================================

    public List<Disaster> searchDisasters(
            String keyword) {

        if (keyword == null ||
                keyword.trim().isEmpty()) {

            return disasterRepository.findByArchivedFalse();
        }

        return disasterRepository
                .findByEventNameContainingIgnoreCase(
                        keyword.trim()
                )
                .stream()
                .filter(disaster -> !disaster.isArchived())
                .toList();
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
        // UPDATE SEVERITY LEVEL - DM-05
        // =====================================================

        existingDisaster.setSeverityLevel(
                updatedDisaster.getSeverityLevel()
        );


        // =====================================================
        // SAVE UPDATED EVENT
        // =====================================================

        return disasterRepository.save(
                existingDisaster
        );
    }


    // =========================================================
    // MOVE FLOOD EVENT TO RECYCLE BIN
    // =========================================================

    public void deleteDisaster(
            Long id) {

        // Find the existing flood event
        Disaster disaster =
                getDisasterById(id);

        // Mark as archived instead of permanently deleting
        disaster.setArchived(true);

        // Save the archived flood event
        disasterRepository.save(disaster);
    }


    // =========================================================
    // GET ARCHIVED FLOOD EVENTS
    // =========================================================

    public List<Disaster> getArchivedDisasters() {

        return disasterRepository.findByArchivedTrue();
    }


    // =========================================================
    // RESTORE FLOOD EVENT FROM RECYCLE BIN
    // =========================================================

    public Disaster restoreDisaster(Long id) {

        Disaster disaster = getDisasterById(id);

        disaster.setArchived(false);

        return disasterRepository.save(disaster);
    }
}
