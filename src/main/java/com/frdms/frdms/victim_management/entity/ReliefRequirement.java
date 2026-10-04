package com.frdms.frdms.victim_management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "relief_requirements")
public class ReliefRequirement {

    // =========================================================
    // ID
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // NEEDS ASSESSMENT
    // =========================================================

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "needs_assessment_id",
            nullable = false
    )
    private NeedsAssessment needsAssessment;


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
    // RECIPIENT NAME
    // =========================================================

    @Column(
            name = "recipient_name",
            nullable = false
    )
    private String recipientName;


    // =========================================================
    // AFFECTED AREA
    // =========================================================

    @Column(
            name = "affected_area",
            nullable = false
    )
    private String affectedArea;


    // =========================================================
    // SHELTER
    // =========================================================

    @Column(name = "shelter")
    private String shelter;


    // =========================================================
    // RELIEF ITEMS
    // =========================================================

    /*
     * Stores the relief items approved by the
     * Relief Coordinator.
     *
     * Example:
     *
     * Dried Food Bag
     * Drinking Water
     * Medical Supplies
     * Hygiene Kit
     * Baby Care Items
     * Bedding
     */

    @Column(
            name = "relief_items",
            columnDefinition = "TEXT"
    )
    private String reliefItems;


    // =========================================================
    // QUANTITIES
    // =========================================================

    /*
     * Quantities decided by the Relief Coordinator.
     *
     * Dried Food Bag is automatically included.
     */

    @Column(name = "dried_food_bag_quantity")
    private Integer driedFoodBagQuantity = 1;

    @Column(name = "drinking_water_quantity")
    private Integer drinkingWaterQuantity = 0;

    @Column(name = "medical_supplies_quantity")
    private Integer medicalSuppliesQuantity = 0;

    @Column(name = "hygiene_kit_quantity")
    private Integer hygieneKitQuantity = 0;

    @Column(name = "baby_care_items_quantity")
    private Integer babyCareItemsQuantity = 0;

    @Column(name = "bedding_quantity")
    private Integer beddingQuantity = 0;


    // =========================================================
    // VICTIM MESSAGE
    // =========================================================

    /*
     * Message/instruction that the victim should receive
     * from the Relief Coordinator.
     */

    @Column(
            name = "victim_message",
            columnDefinition = "TEXT"
    )
    private String victimMessage;


    // =========================================================
    // STATUS
    // =========================================================

    @Column(
            name = "status",
            nullable = false
    )
    private String status = "APPROVED";


    // =========================================================
    // CREATED DATE
    // =========================================================

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    // =========================================================
    // VICTIM NOTIFICATION
    // =========================================================

    @Column(
            name = "victim_notified",
            nullable = false
    )
    private boolean victimNotified = false;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReliefRequirement() {
    }


    // =========================================================
    // GETTER AND SETTER - ID
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    // =========================================================
    // GETTER AND SETTER - NEEDS ASSESSMENT
    // =========================================================

    public NeedsAssessment getNeedsAssessment() {
        return needsAssessment;
    }

    public void setNeedsAssessment(
            NeedsAssessment needsAssessment) {

        this.needsAssessment = needsAssessment;
    }


    // =========================================================
    // GETTER AND SETTER - VICTIM
    // =========================================================

    public Victim getVictim() {
        return victim;
    }

    public void setVictim(Victim victim) {
        this.victim = victim;
    }


    // =========================================================
    // GETTER AND SETTER - RECIPIENT NAME
    // =========================================================

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }


    // =========================================================
    // GETTER AND SETTER - AFFECTED AREA
    // =========================================================

    public String getAffectedArea() {
        return affectedArea;
    }

    public void setAffectedArea(String affectedArea) {
        this.affectedArea = affectedArea;
    }


    // =========================================================
    // GETTER AND SETTER - SHELTER
    // =========================================================

    public String getShelter() {
        return shelter;
    }

    public void setShelter(String shelter) {
        this.shelter = shelter;
    }


    // =========================================================
    // GETTER AND SETTER - RELIEF ITEMS
    // =========================================================

    public String getReliefItems() {
        return reliefItems;
    }

    public void setReliefItems(String reliefItems) {
        this.reliefItems = reliefItems;
    }


    // =========================================================
    // GETTER AND SETTER - DRIED FOOD BAG
    // =========================================================

    public Integer getDriedFoodBagQuantity() {
        return driedFoodBagQuantity;
    }

    public void setDriedFoodBagQuantity(
            Integer driedFoodBagQuantity) {

        this.driedFoodBagQuantity =
                driedFoodBagQuantity != null
                        ? driedFoodBagQuantity
                        : 0;
    }


    // =========================================================
    // GETTER AND SETTER - DRINKING WATER
    // =========================================================

    public Integer getDrinkingWaterQuantity() {
        return drinkingWaterQuantity;
    }

    public void setDrinkingWaterQuantity(
            Integer drinkingWaterQuantity) {

        this.drinkingWaterQuantity =
                drinkingWaterQuantity != null
                        ? drinkingWaterQuantity
                        : 0;
    }


    // =========================================================
    // GETTER AND SETTER - MEDICAL SUPPLIES
    // =========================================================

    public Integer getMedicalSuppliesQuantity() {
        return medicalSuppliesQuantity;
    }

    public void setMedicalSuppliesQuantity(
            Integer medicalSuppliesQuantity) {

        this.medicalSuppliesQuantity =
                medicalSuppliesQuantity != null
                        ? medicalSuppliesQuantity
                        : 0;
    }


    // =========================================================
    // GETTER AND SETTER - HYGIENE KIT
    // =========================================================

    public Integer getHygieneKitQuantity() {
        return hygieneKitQuantity;
    }

    public void setHygieneKitQuantity(
            Integer hygieneKitQuantity) {

        this.hygieneKitQuantity =
                hygieneKitQuantity != null
                        ? hygieneKitQuantity
                        : 0;
    }


    // =========================================================
    // GETTER AND SETTER - BABY CARE ITEMS
    // =========================================================

    public Integer getBabyCareItemsQuantity() {
        return babyCareItemsQuantity;
    }

    public void setBabyCareItemsQuantity(
            Integer babyCareItemsQuantity) {

        this.babyCareItemsQuantity =
                babyCareItemsQuantity != null
                        ? babyCareItemsQuantity
                        : 0;
    }


    // =========================================================
    // GETTER AND SETTER - BEDDING
    // =========================================================

    public Integer getBeddingQuantity() {
        return beddingQuantity;
    }

    public void setBeddingQuantity(
            Integer beddingQuantity) {

        this.beddingQuantity =
                beddingQuantity != null
                        ? beddingQuantity
                        : 0;
    }


    // =========================================================
    // GETTER AND SETTER - VICTIM MESSAGE
    // =========================================================

    public String getVictimMessage() {
        return victimMessage;
    }

    public void setVictimMessage(String victimMessage) {
        this.victimMessage = victimMessage;
    }


    // =========================================================
    // GETTER AND SETTER - STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    // =========================================================
    // GETTER AND SETTER - CREATED DATE
    // =========================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // =========================================================
    // GETTER AND SETTER - VICTIM NOTIFIED
    // =========================================================

    /*
     * Standard boolean getter.
     */
    public boolean isVictimNotified() {
        return victimNotified;
    }

    /*
     * Additional getter so code using getVictimNotified()
     * also works correctly.
     */
    public boolean getVictimNotified() {
        return victimNotified;
    }

    public void setVictimNotified(
            boolean victimNotified) {

        this.victimNotified = victimNotified;
    }


    // =========================================================
    // AUTOMATICALLY SET CREATION DATE
    // =========================================================

    @PrePersist
    protected void beforeSave() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null
                || status.trim().isEmpty()) {

            status = "APPROVED";
        }


        /*
         * Dried Food Bag is automatically included.
         */
        if (driedFoodBagQuantity == null
                || driedFoodBagQuantity < 1) {

            driedFoodBagQuantity = 1;
        }


        if (drinkingWaterQuantity == null) {
            drinkingWaterQuantity = 0;
        }

        if (medicalSuppliesQuantity == null) {
            medicalSuppliesQuantity = 0;
        }

        if (hygieneKitQuantity == null) {
            hygieneKitQuantity = 0;
        }

        if (babyCareItemsQuantity == null) {
            babyCareItemsQuantity = 0;
        }

        if (beddingQuantity == null) {
            beddingQuantity = 0;
        }
    }
}