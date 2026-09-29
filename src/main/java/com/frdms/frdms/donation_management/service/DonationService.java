package com.frdms.frdms.donation_management.service;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.repository.DonationRepository;
import com.frdms.frdms.finance_management.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DonationService {

    private final DonationRepository donationRepository;

    private final InventoryService inventoryService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DonationService(
            DonationRepository donationRepository,
            InventoryService inventoryService) {

        this.donationRepository = donationRepository;
        this.inventoryService = inventoryService;
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

            donation.setDonationDate(
                    LocalDate.now()
            );
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

    public boolean isWithinEditPeriod(
            Donation donation) {

        // If donation has no date,
        // editing/deleting is not allowed.
        if (donation.getDonationDate() == null) {

            return false;
        }

        long daysPassed =
                ChronoUnit.DAYS.between(
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

        return daysPassed >= 0
                && daysPassed < 2;
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

        return donationRepository.save(
                existingDonation
        );
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

    @Transactional
    public Donation acceptDonation(Long id) {

        Donation donation =
                getDonationById(id);


        // -----------------------------------------------------
        // PREVENT DUPLICATE ACCEPTANCE
        // -----------------------------------------------------

        if ("ACCEPTED".equalsIgnoreCase(
                donation.getStatus())) {

            return donation;
        }


        // -----------------------------------------------------
        // PREVENT ACCEPTING REJECTED DONATION
        // -----------------------------------------------------

        if ("REJECTED".equalsIgnoreCase(
                donation.getStatus())) {

            throw new IllegalStateException(
                    "A rejected donation cannot be accepted"
            );
        }


        // -----------------------------------------------------
        // HANDLE OLD DONATIONS WITHOUT DATE
        // -----------------------------------------------------

        if (donation.getDonationDate() == null) {

            donation.setDonationDate(
                    LocalDate.now()
            );
        }


        // =====================================================
        // GOODS DONATION → INVENTORY
        // =====================================================

        if ("GOODS".equalsIgnoreCase(
                donation.getDonationType())) {


            // -------------------------------------------------
            // CHECK GOODS DESCRIPTION
            // -------------------------------------------------

            if (donation.getGoodsDescription() == null
                    || donation.getGoodsDescription()
                    .trim()
                    .isEmpty()) {

                throw new IllegalStateException(
                        "Goods description is required"
                );
            }


            // -------------------------------------------------
            // CHECK GOODS QUANTITY
            // -------------------------------------------------

            if (donation.getGoodsQuantity() == null
                    || donation.getGoodsQuantity()
                    .trim()
                    .isEmpty()) {

                throw new IllegalStateException(
                        "Goods quantity is required"
                );
            }


            // -------------------------------------------------
            // READ GOODS QUANTITY
            // -------------------------------------------------

            String quantityText =
                    donation.getGoodsQuantity()
                            .trim();

            int quantity;


            try {

                /*
                 * Supports:
                 *
                 * 10
                 * 10 kg
                 * 10 kg pack
                 * 50 bottles
                 * 20 boxes
                 *
                 * The first number is used
                 * as the inventory quantity.
                 */

                String numberOnly =
                        quantityText.replaceAll(
                                "^\\D*(\\d+).*",
                                "$1"
                        );

                quantity =
                        Integer.parseInt(numberOnly);

            } catch (Exception e) {

                throw new IllegalStateException(
                        "Invalid goods quantity: "
                                + donation.getGoodsQuantity()
                );
            }


            // -------------------------------------------------
            // CHECK QUANTITY
            // -------------------------------------------------

            if (quantity <= 0) {

                throw new IllegalStateException(
                        "Goods quantity must be greater than zero"
                );
            }


            // -------------------------------------------------
            // UPDATE INVENTORY
            // -------------------------------------------------

            inventoryService.addDonationToInventory(

                    donation
                            .getGoodsDescription()
                            .trim(),

                    quantity,

                    donation.getDescription()
            );
        }


        // =====================================================
        // SET DONATION STATUS
        // =====================================================

        donation.setStatus("ACCEPTED");


        // Save accepted donation
        return donationRepository.save(donation);
    }


    // =========================================================
    // FINANCE OFFICER - REJECT DONATION
    // =========================================================

    @Transactional
    public Donation rejectDonation(Long id) {

        Donation donation =
                getDonationById(id);


        // -----------------------------------------------------
        // PREVENT REJECTING ALREADY ACCEPTED DONATION
        // -----------------------------------------------------

        if ("ACCEPTED".equalsIgnoreCase(
                donation.getStatus())) {

            throw new IllegalStateException(
                    "An accepted donation cannot be rejected"
            );
        }


        // -----------------------------------------------------
        // PREVENT DUPLICATE REJECTION
        // -----------------------------------------------------

        if ("REJECTED".equalsIgnoreCase(
                donation.getStatus())) {

            return donation;
        }


        // -----------------------------------------------------
        // HANDLE OLD DONATIONS WITHOUT DATE
        // -----------------------------------------------------

        if (donation.getDonationDate() == null) {

            donation.setDonationDate(
                    LocalDate.now()
            );
        }


        // -----------------------------------------------------
        // SET REJECTED
        // -----------------------------------------------------

        donation.setStatus("REJECTED");


        /*
         * IMPORTANT:
         *
         * Rejected goods are NOT added
         * to inventory.
         */

        return donationRepository.save(donation);
    }

}