package com.frdms.frdms.donation_management.repository;

import com.frdms.frdms.donation_management.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByDonorId(Long donorId);

    List<Donation> findByDonationType(String donationType);

    List<Donation> findByStatus(String status);

    List<Donation> findByDonationDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // ACCEPTED CASH DONATIONS
    // =========================================================

    List<Donation> findByDonationTypeAndStatus(
            String donationType,
            String status
    );
}