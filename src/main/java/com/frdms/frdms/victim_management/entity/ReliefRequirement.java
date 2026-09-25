package com.frdms.frdms.victim_management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "relief_requirements")
public class ReliefRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * The Needs Assessment that this relief decision belongs to.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "needs_assessment_id", nullable = false)
    private NeedsAssessment needsAssessment;

    /*
     * Victim who will receive the relief.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "victim_id", nullable = false)
    private Victim victim;

    /*
     * Relief items approved by the Relief Coordinator.
     * Example:
     * Rice - 10kg
     * Water - 20 bottles
     * Dry ration pack - 1
     */
    @Column(name = "relief_items", columnDefinition = "TEXT")
    private String reliefItems;

    /*
     * Message/instruction that the victim should receive.
     */
    @Column(name = "victim_message", columnDefinition = "TEXT")
    private String victimMessage;

    /*
     * Current status of the relief decision.
     */
    @Column(name = "status", nullable = false)
    private String status = "APPROVED";

    /*
     * Date/time when RC recorded the relief decision.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    // ---------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------

    public ReliefRequirement() {
    }


    // ---------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NeedsAssessment getNeedsAssessment() {
        return needsAssessment;
    }

    public void setNeedsAssessment(NeedsAssessment needsAssessment) {
        this.needsAssessment = needsAssessment;
    }

    public Victim getVictim() {
        return victim;
    }

    public void setVictim(Victim victim) {
        this.victim = victim;
    }

    public String getReliefItems() {
        return reliefItems;
    }

    public void setReliefItems(String reliefItems) {
        this.reliefItems = reliefItems;
    }

    public String getVictimMessage() {
        return victimMessage;
    }

    public void setVictimMessage(String victimMessage) {
        this.victimMessage = victimMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // ---------------------------------------------------------
    // Automatically set creation date
    // ---------------------------------------------------------

    @PrePersist
    protected void beforeSave() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null || status.trim().isEmpty()) {
            status = "APPROVED";
        }
    }
}