package com.frdms.frdms.victim_management.repository;

import com.frdms.frdms.victim_management.entity.GNReport;
import com.frdms.frdms.victim_management.entity.Victim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GNReportRepository extends JpaRepository<GNReport, Long> {

    Optional<GNReport> findByReportNumber(String reportNumber);

    List<GNReport> findByVictim(Victim victim);

    // Get the latest GN report for a victim
    Optional<GNReport> findFirstByVictimOrderByIdDesc(Victim victim);
}
