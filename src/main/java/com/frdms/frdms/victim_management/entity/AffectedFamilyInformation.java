package com.frdms.frdms.victim_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "affected_family_information")
public class AffectedFamilyInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Total number of family members is required")
    @Min(value = 1, message = "There must be at least 1 family member")
    @Column(name = "total_family_members", nullable = false)
    private Integer totalFamilyMembers;

    @NotNull(message = "Number of children below 12 is required")
    @Min(value = 0, message = "Number cannot be negative")
    @Column(name = "children_below_12", nullable = false)
    private Integer childrenBelow12;

    @NotNull(message = "Number of male members is required")
    @Min(value = 0, message = "Number cannot be negative")
    @Column(name = "male_members", nullable = false)
    private Integer maleMembers;

    @NotNull(message = "Number of female members is required")
    @Min(value = 0, message = "Number cannot be negative")
    @Column(name = "female_members", nullable = false)
    private Integer femaleMembers;

    @NotNull(message = "Please specify whether shelter is required")
    @Column(name = "shelter_required", nullable = false)
    private Boolean shelterRequired;

    @Size(max = 1000, message = "Affected situation must be less than 1000 characters")
    @Column(name = "affected_situation", columnDefinition = "TEXT")
    private String affectedSituation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "victim_id", nullable = false, unique = true)
    private Victim victim;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public AffectedFamilyInformation() {
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public Integer getTotalFamilyMembers() {
        return totalFamilyMembers;
    }

    public Integer getChildrenBelow12() {
        return childrenBelow12;
    }

    public Integer getMaleMembers() {
        return maleMembers;
    }

    public Integer getFemaleMembers() {
        return femaleMembers;
    }

    public Boolean getShelterRequired() {
        return shelterRequired;
    }

    public String getAffectedSituation() {
        return affectedSituation;
    }

    public Victim getVictim() {
        return victim;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setTotalFamilyMembers(Integer totalFamilyMembers) {
        this.totalFamilyMembers = totalFamilyMembers;
    }

    public void setChildrenBelow12(Integer childrenBelow12) {
        this.childrenBelow12 = childrenBelow12;
    }

    public void setMaleMembers(Integer maleMembers) {
        this.maleMembers = maleMembers;
    }

    public void setFemaleMembers(Integer femaleMembers) {
        this.femaleMembers = femaleMembers;
    }

    public void setShelterRequired(Boolean shelterRequired) {
        this.shelterRequired = shelterRequired;
    }

    public void setAffectedSituation(String affectedSituation) {
        this.affectedSituation = affectedSituation;
    }

    public void setVictim(Victim victim) {
        this.victim = victim;
    }
}