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

    /*
     * The Needs Assessment that this relief decision belongs to.
     */

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "needs_assessment_id",
            nullable = false
    )
    private NeedsAssessment needsAssessment;


    // =========================================================
    // VICTIM
    // =========================================================

    /*
     * Victim who will receive the relief.
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "victim_id",
            nullable = false
    )
    private Victim victim;


    // =========================================================
    // RECIPIENT NAME
    // =========================================================

    /*
     * Name of the victim who will receive the relief package.
     *
     * This is automatically taken from the Victim record.
     *
     * Example:
     * Kamal Perera
     */

    @Column(
            name = "recipient_name",
            nullable = false
    )
    private String recipientName;


    // =========================================================
    // AFFECTED AREA
    // =========================================================

    /*
     * Area affected by the disaster.
     *
     * This information is passed to Distribution Management
     * so that the package can be prepared and delivered
     * to the correct area.
     */

    @Column(
            name = "affected_area",
            nullable = false
    )
    private String affectedArea;


    // =========================================================
    // SHELTER
    // =========================================================

    /*
     * Shelter assigned to the victim.
     *
     * This is optional because a victim may not be staying
     * in a shelter.
     *
     * Example:
     * Kandy Central Relief Shelter
     */

    @Column(
            name = "shelter"
    )
    private String shelter;


    // =========================================================
    // RELIEF ITEMS
    // =========================================================

    /*
     * Relief items approved by the Relief Coordinator.
     *
     * The following two items are mandatory:
     *
     * 1. Dried Food Bag
     * 2. 10 kg Rice Bag
     *
     * Other items may also be included.
     *
     * Example:
     *
     * Dried Food Bag |
     * 10 kg Rice Bag |
     * Drinking Water |
     * Hygiene Kit
     */

    @Column(
            name = "relief_items",
            columnDefinition = "TEXT"
    )
    private String reliefItems;


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

    /*
     * Current status of the relief requirement.
     *
     * Possible values:
     *
     * PENDING
     * APPROVED
     * READY FOR DISTRIBUTION
     * COMPLETED
     */

    @Column(
            name = "status",
            nullable = false
    )
    private String status = "APPROVED";


    // =========================================================
    // CREATED DATE
    // =========================================================

    /*
     * Date/time when the Relief Coordinator
     * recorded the relief requirement.
     */

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    // =========================================================
    // VICTIM NOTIFICATION
    // =========================================================

    /*
     * Indicates whether the confirmed relief requirement
     * has been sent/confirmed to the victim.
     *
     * false = Not Sent
     * true  = Sent to Victim
     */

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

    public boolean isVictimNotified() {
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

        // Automatically set creation date
        if (createdAt == null) {

            createdAt = LocalDateTime.now();
        }

        // Automatically set default status
        if (status == null
                || status.trim().isEmpty()) {

            status = "APPROVED";
        }
    }
}