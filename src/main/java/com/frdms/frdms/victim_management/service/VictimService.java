package com.frdms.frdms.victim_management.service;

import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.repository.VictimRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VictimService {

    private final VictimRepository victimRepository;
    private final PasswordEncoder passwordEncoder;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimService(
            VictimRepository victimRepository,
            PasswordEncoder passwordEncoder) {

        this.victimRepository = victimRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // =========================================================
    // VM-01
    // REGISTER NEW VICTIM
    // =========================================================

    public Victim registerVictim(Victim victim) {

        String encodedPassword =
                passwordEncoder.encode(
                        victim.getPassword()
                );

        victim.setPassword(
                encodedPassword
        );

        return victimRepository.save(
                victim
        );
    }


    // =========================================================
    // CHECK PASSWORD
    // =========================================================

    public boolean checkPassword(
            String enteredPassword,
            String storedPassword) {

        if (
                enteredPassword == null
                        ||
                        storedPassword == null
        ) {
            return false;
        }

        return passwordEncoder.matches(
                enteredPassword,
                storedPassword
        );
    }


    // =========================================================
    // GET ALL VICTIMS
    // =========================================================

    public List<Victim> getAllVictims() {

        return victimRepository.findAll();
    }


    // =========================================================
    // GET VICTIM BY ID
    // =========================================================

    public Victim getVictimById(Long id) {

        return victimRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Victim not found with ID: "
                                                + id
                                )
                );
    }


    // =========================================================
    // FIND VICTIM BY NIC
    // =========================================================

    public Victim getVictimByNic(
            String nicNumber) {

        if (
                nicNumber == null
                        ||
                        nicNumber.trim().isEmpty()
        ) {
            return null;
        }

        return victimRepository
                .findByNicNumber(
                        nicNumber.trim()
                )
                .orElse(null);
    }


    // =========================================================
    // UPDATE VICTIM
    // =========================================================

    public Victim updateVictim(
            Long id,
            Victim updatedVictim) {

        Victim existingVictim =
                getVictimById(id);


        // -----------------------------------------------------
        // UPDATE FULL NAME
        // -----------------------------------------------------

        existingVictim.setFullName(
                updatedVictim.getFullName()
        );


        // -----------------------------------------------------
        // UPDATE NIC
        // -----------------------------------------------------

        existingVictim.setNicNumber(
                updatedVictim.getNicNumber()
        );


        // -----------------------------------------------------
        // UPDATE PHONE
        // -----------------------------------------------------

        existingVictim.setPhoneNumber(
                updatedVictim.getPhoneNumber()
        );


        // -----------------------------------------------------
        // UPDATE ADDRESS
        // -----------------------------------------------------

        existingVictim.setAddress(
                updatedVictim.getAddress()
        );


        // Password is intentionally NOT changed
        // during a normal victim information update.

        return victimRepository.save(
                existingVictim
        );
    }


    // =========================================================
    // DELETE VICTIM
    // =========================================================

    public void deleteVictim(Long id) {

        Victim victim =
                getVictimById(id);

        victimRepository.delete(
                victim
        );
    }
}