package com.frdms.frdms.shelter_management.entity;

import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import jakarta.persistence.*;

@Entity
@Table(
        name = "shelter_allocations",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "affected_family_id"
                )
        }
)
public class ShelterAllocation {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // AFFECTED FAMILY
    // =========================================================

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "affected_family_id",
            nullable = false,
            unique = true
    )
    private AffectedFamilyInformation affectedFamily;


    // =========================================================
    // SHELTER
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "shelter_id",
            nullable = false
    )
    private Shelter shelter;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ShelterAllocation() {
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public AffectedFamilyInformation getAffectedFamily() {
        return affectedFamily;
    }

    public Shelter getShelter() {
        return shelter;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setAffectedFamily(
            AffectedFamilyInformation affectedFamily
    ) {
        this.affectedFamily = affectedFamily;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }
}
