package com.frdms.frdms.victim_management.service;

import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.repository.VictimRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VictimService {

    private final VictimRepository victimRepository;

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimService(VictimRepository victimRepository) {
        this.victimRepository = victimRepository;
    }


    // =========================================================
    // VM-01
    // REGISTER NEW VICTIM
    // =========================================================

    public Victim registerVictim(Victim victim) {

        /*
         * The password entered by the victim must NOT
         * be stored directly in the database.
         *
         * BCrypt converts the password into a secure hash.
         */

        String encodedPassword =
                passwordEncoder.encode(victim.getPassword());

        victim.setPassword(encodedPassword);

        return victimRepository.save(victim);
    }


    // =========================================================
    // CHECK PASSWORD
    // Used during victim login
    // =========================================================

    public boolean checkPassword(
            String enteredPassword,
            String storedPassword) {

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

        return victimRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Victim not found with ID: " + id
                        )
                );
    }


    // =========================================================
    // FIND VICTIM BY NIC
    // Used for victim identification and verification
    // =========================================================

    public Victim getVictimByNic(String nicNumber) {

        return victimRepository
                .findByNicNumber(nicNumber)
                .orElse(null);
    }


    // =========================================================
    // FIND VICTIM BY USERNAME
    // Used for victim login
    // =========================================================

    public Victim getVictimByUsername(String username) {

        return victimRepository
                .findByUsername(username)
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


        existingVictim.setFullName(
                updatedVictim.getFullName()
        );


        existingVictim.setNicNumber(
                updatedVictim.getNicNumber()
        );


        existingVictim.setPhoneNumber(
                updatedVictim.getPhoneNumber()
        );


        existingVictim.setAddress(
                updatedVictim.getAddress()
        );


        /*
         * Username is used for login.
         *
         * We keep the existing username unchanged
         * when normal victim information is updated.
         */

        /*
         * We do NOT change the password here.
         *
         * The victim's existing password should remain
         * unchanged when updating normal information.
         */


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

        victimRepository.delete(victim);
    }
}