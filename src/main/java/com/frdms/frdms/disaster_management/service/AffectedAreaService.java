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
    // =========================================================

    public AffectedArea createAffectedArea(
            AffectedArea affectedArea,
            Disaster disaster) {

        // Connect the affected area to the selected disaster
        affectedArea.setDisaster(disaster);

        // Save affected area to MySQL
        return affectedAreaRepository.save(affectedArea);
    }


    // =========================================================
    // GET AFFECTED AREAS BY DISASTER
    // =========================================================

    public List<AffectedArea> getAffectedAreasByDisaster(
            Disaster disaster) {

        return affectedAreaRepository.findByDisaster(disaster);
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
    // =========================================================

    public AffectedArea updateAffectedArea(
            Long id,
            AffectedArea updatedAffectedArea) {

        // Find the existing record
        AffectedArea existingAffectedArea =
                getAffectedAreaById(id);


        // =====================================================
        // UPDATE AREA NAME
        // =====================================================

        existingAffectedArea.setAreaName(
                updatedAffectedArea.getAreaName()
        );


        // =====================================================
        // UPDATE AREA DESCRIPTION
        // =====================================================

        existingAffectedArea.setDescription(
                updatedAffectedArea.getDescription()
        );


        // =====================================================
        // KEEP ORIGINAL DISASTER
        // =====================================================

        /*
         * Do NOT replace the disaster relationship here.
         *
         * The existing affected area already belongs to the
         * correct flood event. Keeping this relationship prevents
         * the disaster_id from becoming null or incorrect.
         */

        Disaster existingDisaster =
                existingAffectedArea.getDisaster();

        existingAffectedArea.setDisaster(
                existingDisaster
        );


        // =====================================================
        // SAVE UPDATED AFFECTED AREA
        // =====================================================

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