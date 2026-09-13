package com.frdms.frdms.shelter_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "shelters")
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Shelter name is required")
    @Column(nullable = false, length = 150)
    private String shelterName;

    @NotBlank(message = "Location is required")
    @Column(nullable = false, length = 255)
    private String location;

    @NotBlank(message = "Contact person is required")
    @Column(nullable = false, length = 150)
    private String contactPerson;

    @NotBlank(message = "Contact number is required")
    @Pattern(
            regexp = "^0\\d{9}$",
            message = "Contact number must be a valid 10-digit number starting with 0"
    )
    @Column(nullable = false, length = 20)
    private String contactNumber;

    @NotNull(message = "Maximum capacity is required")
    @Min(value = 1, message = "Maximum capacity must be at least 1")
    @Column(nullable = false)
    private Integer maximumCapacity;

    @NotNull(message = "Current occupancy is required")
    @Min(value = 0, message = "Current occupancy cannot be negative")
    @Column(nullable = false)
    private Integer currentOccupancy = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShelterStatus status = ShelterStatus.AVAILABLE;

    public Shelter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShelterName() {
        return shelterName;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Integer getMaximumCapacity() {
        return maximumCapacity;
    }

    public void setMaximumCapacity(Integer maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }

    public Integer getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(Integer currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public ShelterStatus getStatus() {
        return status;
    }

    public void setStatus(ShelterStatus status) {
        this.status = status;
    }

    public Integer getAvailableCapacity() {

        if (maximumCapacity == null || currentOccupancy == null) {
            return 0;
        }

        return maximumCapacity - currentOccupancy;
    }

    public enum ShelterStatus {
        AVAILABLE,
        FULL,
        CLOSED
    }
}