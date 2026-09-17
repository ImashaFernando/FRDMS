package com.frdms.frdms.donation_management.service;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.repository.DonationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DonationService {

    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }


    // =========================================================
    // CREATE DONATION
    // =========================================================

    public Donation submitDonation(Donation donation) {

        // Set default status
        if (donation.getStatus() == null ||
                donation.getStatus().isBlank()) {

            donation.setStatus("PENDING");
        }

        // Automatically set today's date
        if (donation.getDonationDate() == null) {
            donation.setDonationDate(LocalDate.now());
        }

        return donationRepository.save(donation);
    }


    // =========================================================
    // GET DONATIONS
    // =========================================================

    public List<Donation> getAllDonations() {

        return donationRepository.findAll();
    }


    public List<Donation> getCashDonations() {

        return donationRepository.findByDonationType("CASH");
    }


    public List<Donation> getGoodsDonations() {

        return donationRepository.findByDonationType("GOODS");
    }


    // =========================================================
    // GET DONATIONS BY DONOR
    // =========================================================

    public List<Donation> getDonationsByDonor(Long donorId) {

        return donationRepository.findByDonorId(donorId);
    }


    // =========================================================
    // GET DONATIONS FOR MONTHLY REPORT
    // =========================================================

    public List<Donation> getDonationsForMonth(
            LocalDate startDate,
            LocalDate endDate) {

        return donationRepository.findByDonationDateBetween(
                startDate,
                endDate
        );
    }


    // =========================================================
    // GET SINGLE DONATION
    // =========================================================

    public Donation getDonationById(Long id) {

        return donationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Donation not found"
                        ));
    }


    // =========================================================
    // CHECK 2-DAY EDIT / DELETE PERIOD
    // =========================================================

    public boolean isWithinEditPeriod(Donation donation) {

        // If donation has no date,
        // editing/deleting is not allowed.
        if (donation.getDonationDate() == null) {
            return false;
        }

        long daysPassed = ChronoUnit.DAYS.between(
                donation.getDonationDate(),
                LocalDate.now()
        );

        /*
         * Allowed:
         *
         * Day 0 → YES
         * Day 1 → YES
         *
         * Day 2 → NO
         * Day 3 → NO
         */

        return daysPassed >= 0 && daysPassed < 2;
    }


    // =========================================================
    // UPDATE DONATION
    // =========================================================

    public Donation updateDonation(
            Long id,
            Long donorId,
            Donation updatedDonation) {

        // Find existing donation
        Donation existingDonation =
                getDonationById(id);


        // -----------------------------------------------------
        // SECURITY CHECK
        // -----------------------------------------------------

        if (!existingDonation.getDonorId().equals(donorId)) {

            throw new IllegalStateException(
                    "You can only edit your own donations"
            );
        }


        // -----------------------------------------------------
        // 2-DAY CHECK
        // -----------------------------------------------------

        if (!isWithinEditPeriod(existingDonation)) {

            throw new IllegalStateException(
                    "The 2-day editing period has expired"
            );
        }


        // -----------------------------------------------------
        // UPDATE ALLOWED FIELDS
        // -----------------------------------------------------

        existingDonation.setDonationType(
                updatedDonation.getDonationType()
        );

        existingDonation.setCashAmount(
                updatedDonation.getCashAmount()
        );

        existingDonation.setGoodsDescription(
                updatedDonation.getGoodsDescription()
        );

        existingDonation.setGoodsQuantity(
                updatedDonation.getGoodsQuantity()
        );

        existingDonation.setDescription(
                updatedDonation.getDescription()
        );


        /*
         * Do NOT allow donor to change:
         *
         * donorId
         * donationDate
         * status
         */

        return donationRepository.save(existingDonation);
    }


    // =========================================================
    // DELETE DONATION
    // =========================================================

    public void deleteDonation(
            Long id,
            Long donorId) {

        // Find donation
        Donation donation =
                getDonationById(id);


        // -----------------------------------------------------
        // SECURITY CHECK
        // -----------------------------------------------------

        if (!donation.getDonorId().equals(donorId)) {

            throw new IllegalStateException(
                    "You can only delete your own donations"
            );
        }


        // -----------------------------------------------------
        // 2-DAY CHECK
        // -----------------------------------------------------

        if (!isWithinEditPeriod(donation)) {

            throw new IllegalStateException(
                    "The 2-day deletion period has expired"
            );
        }


        // Delete donation
        donationRepository.delete(donation);
    }


    // =========================================================
    // DASHBOARD STATISTICS
    // =========================================================

    public long getTotalDonationCount() {

        return donationRepository.count();
    }


    public double getTotalCashDonationAmount() {

        return getCashDonations()
                .stream()
                .mapToDouble(d ->
                        d.getCashAmount() != null
                                ? d.getCashAmount()
                                : 0.0)
                .sum();
    }


    public long getTotalGoodsDonationCount() {

        return getGoodsDonations().size();
    }


    public long getPendingDonationCount() {

        return donationRepository
                .findByStatus("PENDING")
                .size();
    }


    // =========================================================
    // FINANCE OFFICER - ACCEPT DONATION
    // =========================================================

    public Donation acceptDonation(Long id) {

        Donation donation = getDonationById(id);

        /*
         * Some older donations may have been created
         * before donationDate was added to the entity.
         *
         * If the date is missing, set today's date
         * before saving the updated status.
         */

        if (donation.getDonationDate() == null) {

            donation.setDonationDate(LocalDate.now());
        }

        donation.setStatus("ACCEPTED");

        return donationRepository.save(donation);
    }


    // =========================================================
    // FINANCE OFFICER - REJECT DONATION
    // =========================================================

    public Donation rejectDonation(Long id) {

        Donation donation = getDonationById(id);

        /*
         * Handle older donations with no donation date.
         */

        if (donation.getDonationDate() == null) {

            donation.setDonationDate(LocalDate.now());
        }

        donation.setStatus("REJECTED");

        return donationRepository.save(donation);
    }

}