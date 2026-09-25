package com.frdms.frdms.victim_management.service;

import com.frdms.frdms.victim_management.entity.GNReport;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.repository.GNReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GNReportService {

    private final GNReportRepository gnReportRepository;

    public GNReportService(GNReportRepository gnReportRepository) {
        this.gnReportRepository = gnReportRepository;
    }

    // Save a new GN report
    public GNReport submitGNReport(GNReport gnReport) {
        return gnReportRepository.save(gnReport);
    }

    // Get all GN reports
    public List<GNReport> getAllGNReports() {
        return gnReportRepository.findAll();
    }

    // Get a GN report by ID
    public GNReport getGNReportById(Long id) {
        return gnReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "GN Report not found with ID: " + id
                ));
    }

    // Get GN reports belonging to a victim
    public List<GNReport> getGNReportsByVictim(Victim victim) {
        return gnReportRepository.findByVictim(victim);
    }
    public GNReport getLatestGNReportByVictim(Victim victim) {

        List<GNReport> reports =
                gnReportRepository.findByVictim(victim);

        if (reports == null || reports.isEmpty()) {
            return null;
        }

        return reports.get(reports.size() - 1);
    }

    // Find report by report number
    public GNReport getGNReportByNumber(String reportNumber) {
        return gnReportRepository.findByReportNumber(reportNumber)
                .orElse(null);
    }

    // Update an existing GN report
    public GNReport updateGNReport(Long id, GNReport updatedReport) {

        GNReport existingReport = getGNReportById(id);

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

        return gnReportRepository.save(existingReport);
    }

    // Delete a GN report
    public void deleteGNReport(Long id) {

        GNReport gnReport = getGNReportById(id);

        gnReportRepository.delete(gnReport);
    }
}