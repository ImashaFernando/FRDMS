package com.frdms.frdms.disaster_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "disasters")
public class Disaster {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // FLOOD EVENT NAME
    // =========================================================

    @NotBlank(message = "Flood event name is required")
    @Column(name = "event_name", nullable = false)
    private String eventName;


    // =========================================================
    // EVENT DATE
    // =========================================================

    @NotNull(message = "Event date is required")
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;


    // =========================================================
    // EVENT DESCRIPTION
    // =========================================================

    @NotBlank(message = "Description is required")
    @Column(
            name = "description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;


    // =========================================================
    // SEVERITY LEVEL - DM-05
    // =========================================================

    @NotBlank(message = "Severity level is required")
    @Column(
            name = "severity_level",
            nullable = false
    )
    private String severityLevel;


    // =========================================================
    // AFFECTED AREAS
    // =========================================================

    /*
     * One flood event can have many affected areas.
     *
     * Example:
     *
     * Flood Event:
     * "2026 Kelani River Flood"
     *
     * Affected Areas:
     * - Kelaniya
     * - Wattala
     * - Colombo
     *
     * The actual affected-area information is stored
     * in the AffectedArea entity.
     */

    @OneToMany(
            mappedBy = "disaster",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AffectedArea> affectedAreas = new ArrayList<>();


    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public Disaster() {
    }


    public Disaster(
            String eventName,
            LocalDate eventDate,
            String description,
            String severityLevel) {

        this.eventName = eventName;
        this.eventDate = eventDate;
        this.description = description;
        this.severityLevel = severityLevel;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }


    public String getEventName() {
        return eventName;
    }


    public LocalDate getEventDate() {
        return eventDate;
    }


    public String getDescription() {
        return description;
    }


    public String getSeverityLevel() {
        return severityLevel;
    }


    public List<AffectedArea> getAffectedAreas() {
        return affectedAreas;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }


    public void setAffectedAreas(
            List<AffectedArea> affectedAreas) {

        this.affectedAreas = affectedAreas;
    }


    // =========================================================
    // ADD AFFECTED AREA
    // =========================================================

    public void addAffectedArea(
            AffectedArea affectedArea) {

        affectedAreas.add(affectedArea);

        affectedArea.setDisaster(this);
    }


    // =========================================================
    // REMOVE AFFECTED AREA
    // =========================================================

    public void removeAffectedArea(
            AffectedArea affectedArea) {

        affectedAreas.remove(affectedArea);

        affectedArea.setDisaster(null);
    }
}