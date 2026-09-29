package com.frdms.frdms.donation_management.service;

import com.frdms.frdms.donation_management.entity.Donor;
import com.frdms.frdms.donation_management.repository.DonorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonorService {

    private final DonorRepository donorRepository;
    private final PasswordEncoder passwordEncoder;

    public DonorService(DonorRepository donorRepository,
                        PasswordEncoder passwordEncoder) {
        this.donorRepository = donorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // US-D01: Register donor
    public Donor registerDonor(Donor donor) {

        if (donorRepository.existsByUsername(donor.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        donor.setPassword(
                passwordEncoder.encode(donor.getPassword())
        );

        return donorRepository.save(donor);
    }

    // US-D02: View donor details
    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public Donor getDonorById(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Donor not found"));
    }

    // US-D03: Update donor information
    public Donor updateDonor(Long id, Donor updatedDonor) {

        Donor existingDonor = getDonorById(id);

        existingDonor.setUsername(updatedDonor.getUsername());
        existingDonor.setFullName(updatedDonor.getFullName());
        existingDonor.setContactNumber(updatedDonor.getContactNumber());
        existingDonor.setEmail(updatedDonor.getEmail());
        existingDonor.setAddress(updatedDonor.getAddress());

        // Update password only if a new password is provided
        if (updatedDonor.getPassword() != null
                && !updatedDonor.getPassword().isBlank()) {

            existingDonor.setPassword(
                    passwordEncoder.encode(updatedDonor.getPassword())
            );
        }

        return donorRepository.save(existingDonor);
    }
    // US-D04: Search donor records
    public List<Donor> searchDonors(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDonors();
        }

        return donorRepository.findByFullNameContainingIgnoreCase(
                keyword.trim()
        );
    }
}