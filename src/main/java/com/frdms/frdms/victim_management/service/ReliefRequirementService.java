package com.frdms.frdms.victim_management.service;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.ReliefRequirement;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.repository.ReliefRequirementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("victimReliefRequirementService")
public class ReliefRequirementService {

    private final ReliefRequirementRepository reliefRequirementRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReliefRequirementService(
            ReliefRequirementRepository reliefRequirementRepository) {

        this.reliefRequirementRepository =
                reliefRequirementRepository;
    }


    // =========================================================
    // CREATE / SAVE RELIEF REQUIREMENT
    // =========================================================

    @Transactional
    public ReliefRequirement createReliefRequirement(
            ReliefRequirement reliefRequirement) {

        if (reliefRequirement == null) {
            throw new IllegalArgumentException(
                    "Relief requirement cannot be null."
            );
        }

        /*
         * Check whether a relief requirement already exists
         * for this Needs Assessment.
         *
         * If it exists, update that existing database record.
         * This prevents a new empty/default record from being
         * created every time the RC opens the form.
         */
        NeedsAssessment needsAssessment =
                reliefRequirement.getNeedsAssessment();

        if (needsAssessment != null) {

            Optional<ReliefRequirement> existingRequirement =
                    reliefRequirementRepository
                            .findByNeedsAssessment(
                                    needsAssessment
                            );

            if (existingRequirement.isPresent()) {

                ReliefRequirement existing =
                        existingRequirement.get();

                /*
                 * Keep the existing database record and update
                 * its values with the values entered by the
                 * Relief Coordinator.
                 */

                existing.setNeedsAssessment(
                        needsAssessment
                );

                existing.setVictim(
                        reliefRequirement.getVictim()
                );

                existing.setRecipientName(
                        reliefRequirement.getRecipientName()
                );

                existing.setShelter(
                        reliefRequirement.getShelter()
                );

                existing.setAffectedArea(
                        reliefRequirement.getAffectedArea()
                );


                // -------------------------------------------------
                // RELIEF QUANTITIES
                // -------------------------------------------------

                existing.setDriedFoodBagQuantity(
                        reliefRequirement
                                .getDriedFoodBagQuantity()
                );

                existing.setDrinkingWaterQuantity(
                        reliefRequirement
                                .getDrinkingWaterQuantity()
                );

                existing.setMedicalSuppliesQuantity(
                        reliefRequirement
                                .getMedicalSuppliesQuantity()
                );

                existing.setHygieneKitQuantity(
                        reliefRequirement
                                .getHygieneKitQuantity()
                );

                existing.setBabyCareItemsQuantity(
                        reliefRequirement
                                .getBabyCareItemsQuantity()
                );

                existing.setBeddingQuantity(
                        reliefRequirement
                                .getBeddingQuantity()
                );


                // -------------------------------------------------
                // OTHER INFORMATION
                // -------------------------------------------------

                existing.setReliefItems(
                        reliefRequirement
                                .getReliefItems()
                );

                existing.setVictimMessage(
                        reliefRequirement
                                .getVictimMessage()
                );

                existing.setStatus(
                        reliefRequirement
                                .getStatus()
                );


                /*
                 * Do NOT call getVictimNotified() here.
                 *
                 * Your ReliefRequirement entity does not contain
                 * that getter. The controller already sets
                 * victimNotified before calling this service.
                 */


                /*
                 * Because 'existing' is the entity already stored
                 * in the database, save() updates that existing
                 * record instead of creating another requirement.
                 */
                return reliefRequirementRepository.save(
                        existing
                );
            }
        }


        // =========================================================
        // NEW RELIEF REQUIREMENT
        // =========================================================

        /*
         * If no requirement exists for this Needs Assessment,
         * save the new requirement normally.
         */
        return reliefRequirementRepository.save(
                reliefRequirement
        );
    }


    // =========================================================
    // GET RELIEF REQUIREMENT BY ID
    // =========================================================

    public ReliefRequirement getReliefRequirementById(
            Long id) {

        return reliefRequirementRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Relief requirement not found with ID: "
                                        + id
                        )
                );
    }


    // =========================================================
    // GET RELIEF REQUIREMENT BY NEEDS ASSESSMENT
    // =========================================================

    public Optional<ReliefRequirement> getByNeedsAssessment(
            NeedsAssessment needsAssessment) {

        if (needsAssessment == null) {
            return Optional.empty();
        }

        return reliefRequirementRepository
                .findByNeedsAssessment(
                        needsAssessment
                );
    }


    // =========================================================
    // GET ALL RELIEF REQUIREMENTS FOR A VICTIM
    // =========================================================

    public List<ReliefRequirement> getByVictim(
            Victim victim) {

        return reliefRequirementRepository
                .findByVictim(
                        victim
                );
    }


    // =========================================================
    // GET ALL RELIEF REQUIREMENTS
    // =========================================================

    public List<ReliefRequirement> getAllReliefRequirements() {

        return reliefRequirementRepository.findAll();
    }


    // =========================================================
    // GET BY STATUS
    // =========================================================

    public List<ReliefRequirement> getByStatus(
            String status) {

        return reliefRequirementRepository
                .findByStatus(
                        status
                );
    }


    // =========================================================
    // UPDATE RELIEF REQUIREMENT
    // =========================================================

    @Transactional
    public ReliefRequirement updateReliefRequirement(
            Long id,
            String reliefItems,
            String victimMessage,
            String status) {

        ReliefRequirement existing =
                getReliefRequirementById(id);

        existing.setReliefItems(
                reliefItems
        );

        existing.setVictimMessage(
                victimMessage
        );

        if (status != null
                && !status.trim().isEmpty()) {

            existing.setStatus(
                    status.trim()
            );
        }

        return reliefRequirementRepository.save(
                existing
        );
    }


    // =========================================================
    // DELETE RELIEF REQUIREMENT
    // =========================================================

    @Transactional
    public void deleteReliefRequirement(
            Long id) {

        if (!reliefRequirementRepository
                .existsById(id)) {

            throw new IllegalArgumentException(
                    "Relief requirement not found with ID: "
                            + id
            );
        }

        reliefRequirementRepository.deleteById(
                id
        );
    }
}