package com.frdms.frdms.distribution_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Driver name is required")
    @Size(max = 100, message = "Driver name cannot exceed 100 characters")
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Driver name can contain only letters, spaces, dots, apostrophes and hyphens"
    )
    @Column(name = "driver_name", nullable = false, length = 100)
    private String driverName;

    @NotBlank(message = "Driver phone number is required")
    @Pattern(
            regexp = "^(0\\d{9}|\\+94\\d{9})$",
            message = "Enter a valid Sri Lankan phone number"
    )
    @Column(name = "driver_phone_number", nullable = false, length = 20)
    private String driverPhoneNumber;

    @NotBlank(message = "Driver license number is required")
    @Size(max = 50, message = "Driver license number cannot exceed 50 characters")
    @Column(name = "driver_license_number", nullable = false, length = 50)
    private String driverLicenseNumber;

    @NotBlank(message = "Available area is required")
    @Size(max = 100, message = "Available area cannot exceed 100 characters")
    @Column(name = "available_area", nullable = false, length = 100)
    private String availableArea;

    @Size(max = 500, message = "Driver description cannot exceed 500 characters")
    @Column(name = "driver_description", length = 500)
    private String driverDescription;


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getAvailableArea() {
        return availableArea;
    }

    public void setAvailableArea(String availableArea) {
        this.availableArea = availableArea;
    }

    public String getDriverDescription() {
        return driverDescription;
    }

    public void setDriverDescription(String driverDescription) {
        this.driverDescription = driverDescription;
    }
}