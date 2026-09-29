package com.frdms.frdms.victim_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "gn_reports")
public class GNReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // GN REPORT NUMBER
    // =========================================================

    @NotBlank(message = "GN report number is required")
    @Column(name = "report_number", nullable = false, unique = true)
    private String reportNumber;


    // =========================================================
    // GN OFFICER
    // =========================================================

    @NotBlank(message = "GN officer name is required")
    @Column(name = "gn_officer_name", nullable = false)
    private String gnOfficerName;


    // =========================================================
    // GN DIVISION
    // =========================================================

    @NotBlank(message = "GN division is required")
    @Column(name = "gn_division", nullable = false)
    private String gnDivision;


    // =========================================================
    // REPORT DESCRIPTION
    // =========================================================

    @NotBlank(message = "Report description is required")
    @Column(
            name = "report_description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String reportDescription;


    // =========================================================
    // SUBMITTED DATE
    // =========================================================

    @Column(name = "submitted_date", nullable = false)
    private LocalDateTime submittedDate;


    // =========================================================
    // UPLOADED FILE
    // =========================================================

    @Lob
    @Column(
            name = "report_file",
            columnDefinition = "LONGBLOB"
    )
    private byte[] reportFile;


    // =========================================================
    // FILE NAME
    // =========================================================

    @Column(name = "file_name")
    private String fileName;


    // =========================================================
    // FILE TYPE
    // =========================================================

    @Column(name = "file_type")
    private String fileType;


    // =========================================================
    // VERIFICATION STATUS
    //
    // PENDING  = Waiting for PM
    // APPROVED = PM approved
    // REJECTED = PM rejected
    // =========================================================

    @Column(
            name = "verification_status",
            nullable = false
    )
    private String verificationStatus = "PENDING";


    // =========================================================
    // VICTIM
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "victim_id",
            nullable = false
    )
    private Victim victim;


    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public GNReport() {
    }


    public GNReport(
            String reportNumber,
            String gnOfficerName,
            String gnDivision,
            String reportDescription,
            Victim victim) {

        this.reportNumber = reportNumber;
        this.gnOfficerName = gnOfficerName;
        this.gnDivision = gnDivision;
        this.reportDescription = reportDescription;
        this.victim = victim;
        this.verificationStatus = "PENDING";
    }


    // =========================================================
    // AUTOMATIC SUBMISSION DATE
    // =========================================================

    @PrePersist
    protected void onCreate() {

        if (submittedDate == null) {

            submittedDate =
                    LocalDateTime.now();
        }

        if (verificationStatus == null) {

            verificationStatus =
                    "PENDING";
        }
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }


    public String getReportNumber() {
        return reportNumber;
    }


    public String getGnOfficerName() {
        return gnOfficerName;
    }


    public String getGnDivision() {
        return gnDivision;
    }


    public String getReportDescription() {
        return reportDescription;
    }


    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }


    public byte[] getReportFile() {
        return reportFile;
    }


    public String getFileName() {
        return fileName;
    }


    public String getFileType() {
        return fileType;
    }


    public String getVerificationStatus() {
        return verificationStatus;
    }


    public Victim getVictim() {
        return victim;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setReportNumber(String reportNumber) {
        this.reportNumber = reportNumber;
    }


    public void setGnOfficerName(String gnOfficerName) {
        this.gnOfficerName = gnOfficerName;
    }


    public void setGnDivision(String gnDivision) {
        this.gnDivision = gnDivision;
    }


    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }


    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }


    public void setReportFile(byte[] reportFile) {
        this.reportFile = reportFile;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }


    public void setVictim(Victim victim) {
        this.victim = victim;
    }
}