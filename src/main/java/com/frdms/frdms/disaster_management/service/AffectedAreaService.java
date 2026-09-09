package com.frdms.frdms.disaster_management.service;

import com.frdms.frdms.disaster_management.entity.AffectedArea;
import com.frdms.frdms.disaster_management.entity.Disaster;
import com.frdms.frdms.disaster_management.repository.AffectedAreaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AffectedAreaService {

    private final AffectedAreaRepository affectedAreaRepository;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AffectedAreaService(
            AffectedAreaRepository affectedAreaRepository) {

        this.affectedAreaRepository = affectedAreaRepository;
    }

    // =========================================================
    // CREATE AFFECTED AREA
    // DM-03 / DM-04
    // =========================================================

    public AffectedArea createAffectedArea(
            AffectedArea affectedArea) {

        return affectedAreaRepository.save(affectedArea);
    }

    // =========================================================
    // GET ALL AFFECTED AREAS FOR A FLOOD EVENT
    // =========================================================

    public List<AffectedArea> getAffectedAreasByDisaster(
            Disaster disaster) {

        return affectedAreaRepository
                .findByDisaster(disaster);
    }

    // =========================================================
    // GET AFFECTED AREA BY ID
    // =========================================================

    public AffectedArea getAffectedAreaById(Long id) {

        return affectedAreaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Affected area not found with ID: " + id
                        )
                );
    }

    // =========================================================
    // UPDATE AFFECTED AREA
    // DM-04
    // =========================================================

    public AffectedArea updateAffectedArea(
            Long id,
            AffectedArea updatedAffectedArea) {

        // Get the existing affected area
        AffectedArea existingAffectedArea =
                getAffectedAreaById(id);

        // Update area name
        existingAffectedArea.setAreaName(
                updatedAffectedArea.getAreaName()
        );

        // Update area description
        existingAffectedArea.setDescription(
                updatedAffectedArea.getDescription()
        );

        /*
         * We intentionally do NOT change the disaster here.
         *
         * The affected area must remain connected
         * to its original flood event.
         */

        return affectedAreaRepository.save(
                existingAffectedArea
        );
    }

    // =========================================================
    // DELETE AFFECTED AREA
    // =========================================================

    public void deleteAffectedArea(Long id) {

        AffectedArea affectedArea =
                getAffectedAreaById(id);

        affectedAreaRepository.delete(affectedArea);
    }
}