package com.frdms.frdms.donation_management.repository;

import com.frdms.frdms.donation_management.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DonationRepository
        extends JpaRepository<Donation, Long> {

    // =========================================================
    // DONATIONS BY DONOR
    // =========================================================

    List<Donation> findByDonorId(Long donorId);


    // =========================================================
    // DONATIONS BY TYPE
    // =========================================================

    List<Donation> findByDonationType(String donationType);


    // Case-insensitive version
    List<Donation> findByDonationTypeIgnoreCase(
            String donationType
    );


    // =========================================================
    // DONATIONS BY STATUS
    // =========================================================

    List<Donation> findByStatus(String status);


    // =========================================================
    // DONATIONS BY DATE RANGE
    // =========================================================

    List<Donation> findByDonationDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );


    // =========================================================
    // DONATIONS BY TYPE + STATUS
    // =========================================================

    List<Donation> findByDonationTypeAndStatus(
            String donationType,
            String status
    );


    // Case-insensitive type + status
    List<Donation> findByDonationTypeIgnoreCaseAndStatusIgnoreCase(
            String donationType,
            String status
    );
}