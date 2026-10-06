package com.frdms.frdms.donation_management.service;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.entity.DonationItem;
import com.frdms.frdms.donation_management.repository.DonationRepository;
import com.frdms.frdms.finance_management.service.InventoryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    // GET ALL DONATIONS
    // =========================================================

    public List<Donation> getAllDonations() {

        return donationRepository.findAll();
    }


    // =========================================================
    // GET CASH DONATIONS
    // =========================================================

    public List<Donation> getCashDonations() {

        return donationRepository.findByDonationType("CASH");
    }
// =========================================================
// GET ACCEPTED GOODS DONATIONS
// =========================================================

    public List<Donation> getAcceptedGoodsDonations() {

        return donationRepository.findByDonationTypeAndStatus(
                "GOODS",
                "ACCEPTED"
        );
    }
    // =========================================================
// GET ACCEPTED CASH DONATIONS
// =========================================================

    public List<Donation> getAcceptedCashDonations() {

        return donationRepository.findByDonationTypeAndStatus(
                "CASH",
                "ACCEPTED"
        );
    }

    // =========================================================
    // GET GOODS DONATIONS
    // =========================================================

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
                        )
                );
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

    @Transactional
    public Donation updateDonation(

            Long id,

            Long donorId,

            Donation updatedDonation,

            List<String> selectedItems,

            List<Integer> itemQuantities,

            List<String> itemUnits,

            String additionalItemName,

            Integer additionalItemQuantity,

            String additionalItemUnit) {


        // =====================================================
        // FIND EXISTING DONATION
        // =====================================================

        Donation existingDonation =
                getDonationById(id);


        // =====================================================
        // SECURITY CHECK
        // =====================================================

        if (!existingDonation.getDonorId()
                .equals(donorId)) {

            throw new IllegalStateException(
                    "You can only edit your own donations"
            );
        }


        // =====================================================
        // 2-DAY CHECK
        // =====================================================

        if (!isWithinEditPeriod(existingDonation)) {

            throw new IllegalStateException(
                    "The 2-day editing period has expired"
            );
        }


        // =====================================================
        // UPDATE BASIC DONATION INFORMATION
        // =====================================================

        existingDonation.setDonationType(
                updatedDonation.getDonationType()
        );

        existingDonation.setCashAmount(
                updatedDonation.getCashAmount()
        );

        existingDonation.setDescription(
                updatedDonation.getDescription()
        );


        // =====================================================
        // GOODS DONATION
        // =====================================================

        if ("GOODS".equalsIgnoreCase(
                updatedDonation.getDonationType())) {


            // =================================================
            // CHECK NORMAL ITEMS
            // =================================================

            boolean hasSelectedItems =
                    selectedItems != null
                            && !selectedItems.isEmpty();


            // =================================================
            // CHECK ADDITIONAL ITEM
            // =================================================

            boolean hasAdditionalItem =
                    additionalItemName != null
                            && !additionalItemName
                            .trim()
                            .isEmpty();


            // =================================================
            // AT LEAST ONE ITEM REQUIRED
            // =================================================

            if (!hasSelectedItems &&
                    !hasAdditionalItem) {

                throw new IllegalStateException(
                        "Please select at least one donation item"
                );
            }


            // =================================================
            // VALIDATE NORMAL ITEMS
            // =================================================

            if (hasSelectedItems) {

                if (itemQuantities == null ||
                        itemUnits == null) {

                    throw new IllegalStateException(
                            "Invalid donation item information"
                    );
                }


                if (selectedItems.size()
                        != itemQuantities.size()) {

                    throw new IllegalStateException(
                            "Item and quantity information does not match"
                    );
                }


                if (selectedItems.size()
                        != itemUnits.size()) {

                    throw new IllegalStateException(
                            "Item and unit information does not match"
                    );
                }


                for (int i = 0;
                     i < selectedItems.size();
                     i++) {


                    String itemName =
                            selectedItems.get(i);


                    Integer quantity =
                            itemQuantities.get(i);


                    String unit =
                            itemUnits.get(i);


                    // Item name
                    if (itemName == null ||
                            itemName.trim().isEmpty()) {

                        throw new IllegalStateException(
                                "Donation item name is required"
                        );
                    }


                    // Quantity
                    if (quantity == null ||
                            quantity <= 0) {

                        throw new IllegalStateException(
                                "Donation item quantity must be greater than 0"
                        );
                    }


                    // Unit
                    if (unit == null ||
                            unit.trim().isEmpty()) {

                        throw new IllegalStateException(
                                "Donation item unit is required"
                        );
                    }
                }
            }


            // =================================================
            // VALIDATE ADDITIONAL ITEM
            // =================================================

            if (hasAdditionalItem) {

                if (additionalItemQuantity == null ||
                        additionalItemQuantity <= 0) {

                    throw new IllegalStateException(
                            "Additional item quantity must be greater than 0"
                    );
                }


                if (additionalItemUnit == null ||
                        additionalItemUnit.trim().isEmpty()) {

                    throw new IllegalStateException(
                            "Additional item unit is required"
                    );
                }
            }


            // =================================================
            // REMOVE OLD DONATION ITEMS
            // =================================================

            existingDonation
                    .getDonationItems()
                    .clear();


            // =================================================
            // CREATE UPDATED ITEMS
            // =================================================

            List<DonationItem> updatedItems =
                    new ArrayList<>();


            // =================================================
            // ADD SELECTED CATALOGUE ITEMS
            // =================================================

            if (hasSelectedItems) {

                for (int i = 0;
                     i < selectedItems.size();
                     i++) {


                    DonationItem item =
                            new DonationItem();


                    item.setDonation(
                            existingDonation
                    );


                    item.setItemName(
                            selectedItems
                                    .get(i)
                                    .trim()
                    );


                    item.setQuantity(
                            itemQuantities.get(i)
                    );


                    item.setUnit(
                            itemUnits
                                    .get(i)
                                    .trim()
                    );


                    updatedItems.add(item);
                }
            }


            // =================================================
            // ADD ADDITIONAL ITEM
            // =================================================

            if (hasAdditionalItem) {

                DonationItem additionalItem =
                        new DonationItem();


                additionalItem.setDonation(
                        existingDonation
                );


                additionalItem.setItemName(
                        additionalItemName
                                .trim()
                );


                additionalItem.setQuantity(
                        additionalItemQuantity
                );


                additionalItem.setUnit(
                        additionalItemUnit
                                .trim()
                );


                updatedItems.add(
                        additionalItem
                );
            }


            // =================================================
            // ADD UPDATED ITEMS TO DONATION
            // =================================================

            existingDonation
                    .getDonationItems()
                    .addAll(updatedItems);


            // =================================================
            // UPDATE OLD GOODS DESCRIPTION
            // =================================================

            StringBuilder goodsDescription =
                    new StringBuilder();


            StringBuilder goodsQuantity =
                    new StringBuilder();


            for (int i = 0;
                 i < updatedItems.size();
                 i++) {


                DonationItem item =
                        updatedItems.get(i);


                if (i > 0) {

                    goodsDescription.append(", ");

                    goodsQuantity.append(", ");
                }


                goodsDescription.append(
                        item.getItemName()
                );


                goodsQuantity.append(
                        item.getQuantity()
                );


                goodsQuantity.append(" ");


                goodsQuantity.append(
                        item.getUnit()
                );
            }


            existingDonation.setGoodsDescription(
                    goodsDescription.toString()
            );


            existingDonation.setGoodsQuantity(
                    goodsQuantity.toString()
            );


        } else {


            // =================================================
            // CASH DONATION
            // =================================================

            existingDonation
                    .getDonationItems()
                    .clear();


            existingDonation.setGoodsDescription(
                    null
            );


            existingDonation.setGoodsQuantity(
                    null
            );
        }


        // =====================================================
        // IMPORTANT
        // =====================================================

        /*
         * DO NOT change:
         *
         * donorId
         * donationDate
         * status
         *
         * These values must remain unchanged during editing.
         */


        // =====================================================
        // SAVE
        // =====================================================

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

        Donation donation =
                getDonationById(id);


        // SECURITY CHECK

        if (!donation.getDonorId()
                .equals(donorId)) {

            throw new IllegalStateException(
                    "You can only delete your own donations"
            );
        }


        // 2-DAY CHECK

        if (!isWithinEditPeriod(donation)) {

            throw new IllegalStateException(
                    "The 2-day deletion period has expired"
            );
        }


        // DELETE

        donationRepository.delete(
                donation
        );
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
                                : 0.0
                )
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


        // =====================================================
        // PREVENT DUPLICATE ACCEPTANCE
        // =====================================================

        if ("ACCEPTED".equalsIgnoreCase(
                donation.getStatus())) {

            return donation;
        }


        // =====================================================
        // PREVENT ACCEPTING REJECTED DONATION
        // =====================================================

        if ("REJECTED".equalsIgnoreCase(
                donation.getStatus())) {

            throw new IllegalStateException(
                    "A rejected donation cannot be accepted"
            );
        }


        // =====================================================
        // HANDLE OLD DONATIONS WITHOUT DATE
        // =====================================================

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


            List<DonationItem> donationItems =
                    donation.getDonationItems();


            // =================================================
            // CHECK ITEMS
            // =================================================

            if (donationItems == null ||
                    donationItems.isEmpty()) {

                throw new IllegalStateException(
                        "No donation items found for this goods donation"
                );
            }


            // =================================================
            // ADD EACH ITEM TO INVENTORY
            // =================================================

            for (DonationItem item :
                    donationItems) {


                // CHECK ITEM NAME

                if (item.getItemName() == null ||
                        item.getItemName()
                                .trim()
                                .isEmpty()) {

                    throw new IllegalStateException(
                            "Donation item name is required"
                    );
                }


                // CHECK QUANTITY

                if (item.getQuantity() == null ||
                        item.getQuantity() <= 0) {

                    throw new IllegalStateException(
                            "Invalid quantity for donation item: "
                                    + item.getItemName()
                    );
                }


                // ADD TO INVENTORY

                inventoryService.addDonationToInventory(

                        item.getItemName()
                                .trim(),

                        item.getQuantity(),

                        item.getUnit(),

                        donation.getDescription()
                );
            }
        }


        // =====================================================
        // SET ACCEPTED
        // =====================================================

        donation.setStatus(
                "ACCEPTED"
        );


        // =====================================================
        // SAVE
        // =====================================================

        return donationRepository.save(
                donation
        );
    }


    // =========================================================
    // FINANCE OFFICER - REJECT DONATION
    // =========================================================

    @Transactional
    public Donation rejectDonation(Long id) {

        Donation donation =
                getDonationById(id);


        // =====================================================
        // PREVENT REJECTING ACCEPTED DONATION
        // =====================================================

        if ("ACCEPTED".equalsIgnoreCase(
                donation.getStatus())) {

            throw new IllegalStateException(
                    "An accepted donation cannot be rejected"
            );
        }


        // =====================================================
        // PREVENT DUPLICATE REJECTION
        // =====================================================

        if ("REJECTED".equalsIgnoreCase(
                donation.getStatus())) {

            return donation;
        }


        // =====================================================
        // HANDLE OLD DONATIONS WITHOUT DATE
        // =====================================================

        if (donation.getDonationDate() == null) {

            donation.setDonationDate(
                    LocalDate.now()
            );
        }


        // =====================================================
        // SET REJECTED
        // =====================================================

        donation.setStatus(
                "REJECTED"
        );


        /*
         * IMPORTANT:
         *
         * Rejected goods are NOT added
         * to inventory.
         */


        // =====================================================
        // SAVE
        // =====================================================

        return donationRepository.save(
                donation
        );
    }
}