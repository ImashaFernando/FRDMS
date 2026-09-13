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

    // US-D05: Submit cash or goods donation
    public Donation submitDonation(Donation donation) {

        if (donation.getStatus() == null || donation.getStatus().isBlank()) {
            donation.setStatus("PENDING");
        }

        return donationRepository.save(donation);
    }

    // US-D06: Get cash donations
    public List<Donation> getCashDonations() {
        return donationRepository.findByDonationType("CASH");
    }

    // US-D07: Get goods donations
    public List<Donation> getGoodsDonations() {
        return donationRepository.findByDonationType("GOODS");
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
    // US-F11: Donation Report - Total donation records
    public long getTotalDonationCount() {
        return donationRepository.count();
    }

    // US-F11: Donation Report - Total cash donation amount
    public double getTotalCashDonationAmount() {
        return getCashDonations()
                .stream()
                .mapToDouble(donation ->
                        donation.getCashAmount() != null
                                ? donation.getCashAmount()
                                : 0.0)
                .sum();
    }

    // US-F11: Donation Report - Total goods donations
    public long getTotalGoodsDonationCount() {
        return getGoodsDonations().size();
    }

    // US-F11: Donation Report - Pending donations
    public long getPendingDonationCount() {
        return donationRepository.findByStatus("PENDING").size();
    }
    // US-D09: Update donation records
    public Donation updateDonation(Long id, Donation updatedDonation) {

        Donation existingDonation = getDonationById(id);

        existingDonation.setDonorId(updatedDonation.getDonorId());
        existingDonation.setDonationType(updatedDonation.getDonationType());
        existingDonation.setCashAmount(updatedDonation.getCashAmount());
        existingDonation.setGoodsDescription(updatedDonation.getGoodsDescription());
        existingDonation.setGoodsQuantity(updatedDonation.getGoodsQuantity());
        existingDonation.setDescription(updatedDonation.getDescription());

        return donationRepository.save(existingDonation);
    }
}