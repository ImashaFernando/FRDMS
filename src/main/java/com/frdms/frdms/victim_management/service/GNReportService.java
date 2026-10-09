package com.frdms.frdms.victim_management.service;

import com.frdms.frdms.victim_management.entity.GNReport;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.repository.GNReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GNReportService {

    private final GNReportRepository gnReportRepository;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public GNReportService(
            GNReportRepository gnReportRepository) {

        this.gnReportRepository =
                gnReportRepository;
    }

    // =========================================================
    // SAVE GN REPORT
    // =========================================================

    public GNReport submitGNReport(
            GNReport gnReport) {

        return gnReportRepository.save(
                gnReport
        );
    }

    // =========================================================
    // GET ALL GN REPORTS
    // =========================================================

    public List<GNReport> getAllGNReports() {

        return gnReportRepository.findAll();
    }

    // =========================================================
    // GET GN REPORT BY ID
    // =========================================================

    public GNReport getGNReportById(Long id) {

        return gnReportRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "GN Report not found with ID: "
                                        + id
                        )
                );
    }

    // =========================================================
    // GET GN REPORTS BY VICTIM
    // =========================================================

    public List<GNReport> getGNReportsByVictim(
            Victim victim) {

        return gnReportRepository
                .findByVictim(victim);
    }

    // =========================================================
    // GET LATEST GN REPORT BY VICTIM
    // =========================================================

    public GNReport getLatestGNReportByVictim(
            Victim victim) {

        return gnReportRepository
                .findFirstByVictimOrderByIdDesc(victim)
                .orElse(null);
    }

    // =========================================================
    // CHECK WHETHER GN REPORT IS APPROVED
    // =========================================================

    /*
     * A victim can access the Needs Assessment
     * ONLY when the latest GN Report is APPROVED.
     *
     * No report  -> false
     * PENDING    -> false
     * REJECTED   -> false
     * APPROVED   -> true
     */

    public boolean isLatestGNReportApproved(
            Victim victim) {

        GNReport latestReport =
                getLatestGNReportByVictim(victim);

        if (latestReport == null) {
            return false;
        }

        return "APPROVED".equalsIgnoreCase(
                latestReport.getVerificationStatus()
        );
    }

    // =========================================================
    // GET GN REPORT BY REPORT NUMBER
    // =========================================================

    public GNReport getGNReportByNumber(
            String reportNumber) {

        return gnReportRepository
                .findByReportNumber(reportNumber)
                .orElse(null);
    }

    // =========================================================
    // UPDATE GN REPORT
    // =========================================================

    public GNReport updateGNReport(
            Long id,
            GNReport updatedReport) {

        GNReport existingReport =
                getGNReportById(id);

        existingReport.setReportNumber(
                updatedReport.getReportNumber()
        );

        existingReport.setGnOfficerName(
                updatedReport.getGnOfficerName()
        );

        existingReport.setGnDivision(
                updatedReport.getGnDivision()
        );

        existingReport.setReportDescription(
                updatedReport.getReportDescription()
        );

        return gnReportRepository.save(
                existingReport
        );
    }

    // =========================================================
    // DELETE GN REPORT
    // =========================================================

    public void deleteGNReport(Long id) {

        GNReport gnReport =
                getGNReportById(id);

        gnReportRepository.delete(gnReport);
    }
}
