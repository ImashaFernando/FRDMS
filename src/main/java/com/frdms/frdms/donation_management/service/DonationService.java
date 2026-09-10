package com.frdms.frdms.donation_management.service;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.repository.DonationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationService {

    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public Donation submitDonation(Donation donation) {
        if (donation.getStatus() == null || donation.getStatus().isBlank()) {
            donation.setStatus("PENDING");
        }

        return donationRepository.save(donation);
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public List<Donation> getDonationsByDonor(Long donorId) {
        return donationRepository.findByDonorId(donorId);
    }

    public Donation getDonationById(Long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
    }
}