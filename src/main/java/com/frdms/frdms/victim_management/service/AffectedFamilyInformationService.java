package com.frdms.frdms.victim_management.service;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.repository.AffectedFamilyInformationRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AffectedFamilyInformationService {

    private final AffectedFamilyInformationRepository repository;

    public AffectedFamilyInformationService(
            AffectedFamilyInformationRepository repository) {

        this.repository = repository;
    }


    // =========================================================
    // SAVE INFORMATION
    // =========================================================

    public AffectedFamilyInformation saveInformation(
            AffectedFamilyInformation information) {

        return repository.save(information);
    }


    // =========================================================
    // GET INFORMATION FOR A SPECIFIC VICTIM
    // =========================================================

    public Optional<AffectedFamilyInformation> getByVictim(
            Victim victim) {

        return repository.findByVictim(victim);
    }


    // =========================================================
    // GET ALL VICTIM FAMILY INFORMATION
    // Used by Relief Coordinator
    // =========================================================

    public List<AffectedFamilyInformation> getAllInformation() {

        return repository.findAll();
    }


    // =========================================================
    // GET INFORMATION BY ID
    // =========================================================

    public AffectedFamilyInformation getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Affected family information not found with ID: "
                                        + id
                        ));
    }


    // =========================================================
    // UPDATE INFORMATION
    // =========================================================

    public AffectedFamilyInformation updateInformation(
            Long id,
            AffectedFamilyInformation updatedInformation) {

        AffectedFamilyInformation existingInformation =
                getById(id);


        existingInformation.setTotalFamilyMembers(
                updatedInformation.getTotalFamilyMembers()
        );


        existingInformation.setChildrenBelow12(
                updatedInformation.getChildrenBelow12()
        );


        existingInformation.setMaleMembers(
                updatedInformation.getMaleMembers()
        );


        existingInformation.setFemaleMembers(
                updatedInformation.getFemaleMembers()
        );


        existingInformation.setShelterRequired(
                updatedInformation.getShelterRequired()
        );


        existingInformation.setAffectedSituation(
                updatedInformation.getAffectedSituation()
        );


        return repository.save(existingInformation);
    }
}