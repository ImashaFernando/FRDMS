package com.frdms.frdms.victim_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "needs_assessments")
public class NeedsAssessment {

    // =========================================================
    // ID
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // VICTIM INFORMATION
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "victim_id", nullable = false)
    private Victim victim;


    // =========================================================
    // INFORMATION PROVIDED BY VICTIM
    // =========================================================

    @NotBlank(message = "Current situation is required")
    @Column(
            name = "current_situation",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String currentSituation;


    /*
     * FOOD
     *
     * Food is no longer shown on the Victim Needs Assessment
     * form.
     *
     * The old database field is kept temporarily so that existing
     * parts of the system that may still reference foodRequired
     * do not break.
     *
     * It is always set to false for new Victim submissions.
     */
    @Column(
            name = "food_required",
            nullable = false
    )
    private Boolean foodRequired = false;


    /*
     * BEDDINGS
     *
     * New assistance option replacing Food on the Victim form.
     */
    @Column(
            name = "bedding_required",
            nullable = false
    )
    private Boolean beddingRequired = false;


    @Column(
            name = "water_required",
            nullable = false
    )
    private Boolean waterRequired = false;


    @Column(
            name = "medical_assistance_required",
            nullable = false
    )
    private Boolean medicalAssistanceRequired = false;


    /*
     * CLOTHING
     *
     * Clothing is no longer shown on the Victim
     * Needs Assessment form.
     *
     * The database column is kept so that the existing
     * database structure is not broken.
     *
     * Default value is false.
     */
    @Column(
            name = "clothing_required",
            nullable = false
    )
    private Boolean clothingRequired = false;


    @Column(
            name = "hygiene_items_required",
            nullable = false
    )
    private Boolean hygieneItemsRequired = false;


    @Column(
            name = "other_needs",
            columnDefinition = "TEXT"
    )
    private String otherNeeds;


    @Column(
            name = "additional_information",
            columnDefinition = "TEXT"
    )
    private String additionalInformation;


    // =========================================================
    // RELIEF COORDINATOR ASSESSMENT
    // =========================================================

    @Column(name = "priority")
    private String priority;


    @Column(
            name = "assessment_remarks",
            columnDefinition = "TEXT"
    )
    private String assessmentRemarks;


    @Column(name = "assessed_by")
    private String assessedBy;


    @Column(name = "assessment_date")
    private LocalDateTime assessmentDate;


    @Column(
            name = "status",
            nullable = false
    )
    private String status = "SUBMITTED";


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public NeedsAssessment() {

        this.foodRequired = false;
        this.beddingRequired = false;
        this.waterRequired = false;
        this.medicalAssistanceRequired = false;
        this.clothingRequired = false;
        this.hygieneItemsRequired = false;
        this.status = "SUBMITTED";
    }


    // =========================================================
    // MAIN CONSTRUCTOR
    // =========================================================

    public NeedsAssessment(
            Victim victim,
            String currentSituation,
            Boolean foodRequired,
            Boolean beddingRequired,
            Boolean waterRequired,
            Boolean medicalAssistanceRequired,
            Boolean clothingRequired,
            Boolean hygieneItemsRequired,
            String otherNeeds,
            String additionalInformation
    ) {

        this.victim = victim;

        this.currentSituation = currentSituation;

        /*
         * Food is no longer collected from the Victim form.
         * Keep the old field for database compatibility.
         */
        this.foodRequired =
                false;

        this.beddingRequired =
                beddingRequired != null
                        ? beddingRequired
                        : false;

        this.waterRequired =
                waterRequired != null
                        ? waterRequired
                        : false;

        this.medicalAssistanceRequired =
                medicalAssistanceRequired != null
                        ? medicalAssistanceRequired
                        : false;

        /*
         * Clothing is no longer collected from the
         * Victim form, therefore default it to false.
         */
        this.clothingRequired =
                false;

        this.hygieneItemsRequired =
                hygieneItemsRequired != null
                        ? hygieneItemsRequired
                        : false;

        this.otherNeeds = otherNeeds;

        this.additionalInformation =
                additionalInformation;

        this.status = "SUBMITTED";
    }


    // =========================================================
    // OLD CONSTRUCTOR
    // =========================================================
    /*
     * This constructor is kept for compatibility with any
     * existing code that may still create NeedsAssessment
     * objects using the previous constructor.
     *
     * Food remains false because Food is no longer collected
     * from the Victim.
     */
    public NeedsAssessment(
            Victim victim,
            String currentSituation,
            Boolean foodRequired,
            Boolean waterRequired,
            Boolean medicalAssistanceRequired,
            Boolean clothingRequired,
            Boolean hygieneItemsRequired,
            String otherNeeds,
            String additionalInformation
    ) {

        this.victim = victim;

        this.currentSituation = currentSituation;

        this.foodRequired = false;

        this.beddingRequired = false;

        this.waterRequired =
                waterRequired != null
                        ? waterRequired
                        : false;

        this.medicalAssistanceRequired =
                medicalAssistanceRequired != null
                        ? medicalAssistanceRequired
                        : false;

        this.clothingRequired = false;

        this.hygieneItemsRequired =
                hygieneItemsRequired != null
                        ? hygieneItemsRequired
                        : false;

        this.otherNeeds = otherNeeds;

        this.additionalInformation =
                additionalInformation;

        this.status = "SUBMITTED";
    }


    // =========================================================
    // AUTOMATICALLY SET VALUES BEFORE DATABASE INSERT
    // =========================================================

    @PrePersist
    protected void onCreate() {

        /*
         * Make sure Boolean fields are never null.
         */

        if (foodRequired == null) {
            foodRequired = false;
        }

        if (beddingRequired == null) {
            beddingRequired = false;
        }

        if (waterRequired == null) {
            waterRequired = false;
        }

        if (medicalAssistanceRequired == null) {
            medicalAssistanceRequired = false;
        }

        if (clothingRequired == null) {
            clothingRequired = false;
        }

        if (hygieneItemsRequired == null) {
            hygieneItemsRequired = false;
        }


        /*
         * Default assessment status.
         */

        if (status == null || status.isBlank()) {
            status = "SUBMITTED";
        }
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }


    public Victim getVictim() {
        return victim;
    }


    public String getCurrentSituation() {
        return currentSituation;
    }


    /*
     * Kept for compatibility with existing project code.
     */
    public Boolean getFoodRequired() {
        return foodRequired;
    }


    public Boolean getBeddingRequired() {
        return beddingRequired;
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


    // =========================================================
    // SETTERS
    // =========================================================

    public void setVictim(Victim victim) {
        this.victim = victim;
    }


    public void setCurrentSituation(
            String currentSituation
    ) {

        this.currentSituation =
                currentSituation;
    }


    /*
     * Kept for compatibility.
     *
     * Food is no longer collected from the Victim form,
     * but existing code can still call this method.
     */
    public void setFoodRequired(
            Boolean foodRequired
    ) {

        this.foodRequired =
                foodRequired != null
                        ? foodRequired
                        : false;
    }


    public void setBeddingRequired(
            Boolean beddingRequired
    ) {

        this.beddingRequired =
                beddingRequired != null
                        ? beddingRequired
                        : false;
    }


    public void setWaterRequired(
            Boolean waterRequired
    ) {

        this.waterRequired =
                waterRequired != null
                        ? waterRequired
                        : false;
    }


    public void setMedicalAssistanceRequired(
            Boolean medicalAssistanceRequired
    ) {

        this.medicalAssistanceRequired =
                medicalAssistanceRequired != null
                        ? medicalAssistanceRequired
                        : false;
    }


    public void setClothingRequired(
            Boolean clothingRequired
    ) {

        this.clothingRequired =
                clothingRequired != null
                        ? clothingRequired
                        : false;
    }


    public void setHygieneItemsRequired(
            Boolean hygieneItemsRequired
    ) {

        this.hygieneItemsRequired =
                hygieneItemsRequired != null
                        ? hygieneItemsRequired
                        : false;
    }


    public void setOtherNeeds(
            String otherNeeds
    ) {

        this.otherNeeds = otherNeeds;
    }


    public void setAdditionalInformation(
            String additionalInformation
    ) {

        this.additionalInformation =
                additionalInformation;
    }


    public void setPriority(
            String priority
    ) {

        this.priority = priority;
    }


    public void setAssessmentRemarks(
            String assessmentRemarks
    ) {

        this.assessmentRemarks =
                assessmentRemarks;
    }


    public void setAssessedBy(
            String assessedBy
    ) {

        this.assessedBy = assessedBy;
    }


    public void setAssessmentDate(
            LocalDateTime assessmentDate
    ) {

        this.assessmentDate =
                assessmentDate;
    }


    public void setStatus(
            String status
    ) {

        this.status = status;
    }
}