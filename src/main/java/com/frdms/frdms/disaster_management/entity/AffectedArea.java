package com.frdms.frdms.disaster_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "affected_areas")
public class AffectedArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Area name is required")
    @Column(name = "area_name", nullable = false)
    private String areaName;

    @NotBlank(message = "Description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Disaster is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disaster_id", nullable = false)
    private Disaster disaster;

    // ================= CONSTRUCTOR =================

    public AffectedArea() {
    }

    public AffectedArea(
            String areaName,
            String description,
            Disaster disaster) {

        this.areaName = areaName;
        this.description = description;
        this.disaster = disaster;
    }

    // ================= GETTERS =================

    public Long getId() {
        return id;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getDescription() {
        return description;
    }

    public Disaster getDisaster() {
        return disaster;
    }

    // ================= SETTERS =================

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDisaster(Disaster disaster) {
        this.disaster = disaster;
    }
}