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

    // US-D01
    public Donor registerDonor(Donor donor) {

        if (donorRepository.existsByUsername(donor.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        donor.setPassword(
                passwordEncoder.encode(donor.getPassword())
        );

        return donorRepository.save(donor);
    }

    // US-D02
    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public Donor getDonorById(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Donor not found"));
    }
}