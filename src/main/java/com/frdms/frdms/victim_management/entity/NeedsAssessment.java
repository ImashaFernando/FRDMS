package com.frdms.frdms.victim_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "needs_assessments")
public class NeedsAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------
    // Victim Information
    // -------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "victim_id", nullable = false)
    private Victim victim;


    // -------------------------
    // Information provided by Victim
    // -------------------------

    @NotBlank(message = "Current situation is required")
    @Column(name = "current_situation", nullable = false, columnDefinition = "TEXT")
    private String currentSituation;

    @NotNull(message = "Please specify whether food is required")
    @Column(name = "food_required", nullable = false)
    private Boolean foodRequired;

    @NotNull(message = "Please specify whether drinking water is required")
    @Column(name = "water_required", nullable = false)
    private Boolean waterRequired;

    @NotNull(message = "Please specify whether medical assistance is required")
    @Column(name = "medical_assistance_required", nullable = false)
    private Boolean medicalAssistanceRequired;

    @NotNull(message = "Please specify whether clothing is required")
    @Column(name = "clothing_required", nullable = false)
    private Boolean clothingRequired;

    @NotNull(message = "Please specify whether hygiene items are required")
    @Column(name = "hygiene_items_required", nullable = false)
    private Boolean hygieneItemsRequired;

    @Column(name = "other_needs", columnDefinition = "TEXT")
    private String otherNeeds;

    @Column(name = "additional_information", columnDefinition = "TEXT")
    private String additionalInformation;


    // -------------------------
    // Relief Coordinator Assessment
    // -------------------------

    @Column(name = "priority")
    private String priority;

    @Column(name = "assessment_remarks", columnDefinition = "TEXT")
    private String assessmentRemarks;

    @Column(name = "assessed_by")
    private String assessedBy;

    @Column(name = "assessment_date")
    private LocalDateTime assessmentDate;

    @Column(name = "status", nullable = false)
    private String status = "SUBMITTED";


    // -------------------------
    // Constructors
    // -------------------------

    public NeedsAssessment() {
    }

    public NeedsAssessment(Victim victim,
                           String currentSituation,
                           Boolean foodRequired,
                           Boolean waterRequired,
                           Boolean medicalAssistanceRequired,
                           Boolean clothingRequired,
                           Boolean hygieneItemsRequired,
                           String otherNeeds,
                           String additionalInformation) {

        this.victim = victim;
        this.currentSituation = currentSituation;
        this.foodRequired = foodRequired;
        this.waterRequired = waterRequired;
        this.medicalAssistanceRequired = medicalAssistanceRequired;
        this.clothingRequired = clothingRequired;
        this.hygieneItemsRequired = hygieneItemsRequired;
        this.otherNeeds = otherNeeds;
        this.additionalInformation = additionalInformation;
        this.status = "SUBMITTED";
    }


    // -------------------------
    // Automatically set date
    // -------------------------

    @PrePersist
    protected void onCreate() {
        if (status == null || status.isBlank()) {
            status = "SUBMITTED";
        }
    }


    // -------------------------
    // Getters
    // -------------------------

    public Long getId() {
        return id;
    }

    public Victim getVictim() {
        return victim;
    }

    public String getCurrentSituation() {
        return currentSituation;
    }

    public Boolean getFoodRequired() {
        return foodRequired;
    }

    public Boolean getWaterRequired() {
        return waterRequired;
    }

    public Boolean getMedicalAssistanceRequired() {
        return medicalAssistanceRequired;
    }

    public Boolean getClothingRequired() {
        return clothingRequired;
    }

    public Boolean getHygieneItemsRequired() {
        return hygieneItemsRequired;
    }

    public String getOtherNeeds() {
        return otherNeeds;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public String getPriority() {
        return priority;
    }

    public String getAssessmentRemarks() {
        return assessmentRemarks;
    }

    public String getAssessedBy() {
        return assessedBy;
    }

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }

    public String getStatus() {
        return status;
    }


    // -------------------------
    // Setters
    // -------------------------

    public void setVictim(Victim victim) {
        this.victim = victim;
    }

    public void setCurrentSituation(String currentSituation) {
        this.currentSituation = currentSituation;
    }

    public void setFoodRequired(Boolean foodRequired) {
        this.foodRequired = foodRequired;
    }

    public void setWaterRequired(Boolean waterRequired) {
        this.waterRequired = waterRequired;
    }

    public void setMedicalAssistanceRequired(Boolean medicalAssistanceRequired) {
        this.medicalAssistanceRequired = medicalAssistanceRequired;
    }

    public void setClothingRequired(Boolean clothingRequired) {
        this.clothingRequired = clothingRequired;
    }

    public void setHygieneItemsRequired(Boolean hygieneItemsRequired) {
        this.hygieneItemsRequired = hygieneItemsRequired;
    }

    public void setOtherNeeds(String otherNeeds) {
        this.otherNeeds = otherNeeds;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setAssessmentRemarks(String assessmentRemarks) {
        this.assessmentRemarks = assessmentRemarks;
    }

    public void setAssessedBy(String assessedBy) {
        this.assessedBy = assessedBy;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}