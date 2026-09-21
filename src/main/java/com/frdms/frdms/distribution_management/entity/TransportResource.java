package com.frdms.frdms.distribution_management.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "transport_resources")
public class TransportResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vehicle Information

    @NotBlank(message = "Vehicle number is required")
    @Size(max = 20, message = "Vehicle number cannot exceed 20 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 -]+$",
            message = "Vehicle number can contain only letters, numbers, spaces and hyphens"
    )
    @Column(name = "vehicle_number", nullable = false, length = 20)
    private String vehicleNumber;
    @NotBlank(message = "Vehicle type is required")
    @Size(max = 50, message = "Vehicle type cannot exceed 50 characters")
    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Size(max = 100, message = "Vehicle model cannot exceed 100 characters")
    @Column(name = "vehicle_model", length = 100)
    private String vehicleModel;

    @Size(max = 50, message = "Vehicle capacity cannot exceed 50 characters")
    @Column(name = "vehicle_capacity", length = 50)
    private String vehicleCapacity;

    @NotBlank(message = "Vehicle owner name is required")
    @Size(max = 100, message = "Owner name cannot exceed 100 characters")
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Owner name can contain only letters, spaces, dots, apostrophes and hyphens"
    )
    @Column(name = "vehicle_owner_name", nullable = false, length = 100)
    private String vehicleOwnerName;

    @NotBlank(message = "Owner phone number is required")
    @Pattern(
            regexp = "^(0\\d{9}|\\+94\\d{9})$",
            message = "Enter a valid Sri Lankan phone number"
    )
    @Column(name = "owner_phone_number", nullable = false, length = 20)
    private String ownerPhoneNumber;
    @NotBlank(message = "Available area is required")
    @Size(max = 100, message = "Available area cannot exceed 100 characters")
    @Column(name = "available_area", nullable = false, length = 100)
    private String availableArea;

    @Size(max = 500, message = "Vehicle description cannot exceed 500 characters")
    @Column(name = "vehicle_description", length = 500)
    private String vehicleDescription;

    // Driver provided by vehicle owner

    @Column(name = "has_driver", nullable = false)
    private Boolean hasDriver = false;

    @Size(max = 100, message = "Driver name cannot exceed 100 characters")
    @Pattern(
            regexp = "^[\\p{L} .'-]*$",
            message = "Driver name can contain only letters, spaces, dots, apostrophes and hyphens"
    )
    @Column(name = "driver_name", length = 100)
    private String driverName;

    @Pattern(
            regexp = "^$|^(0\\d{9}|\\+94\\d{9})$",
            message = "Enter a valid Sri Lankan phone number"
    )
    @Column(name = "driver_phone_number", length = 20)
    private String driverPhoneNumber;

    @Size(max = 50, message = "Driver license number cannot exceed 50 characters")
    @Column(name = "driver_license_number", length = 50)
    private String driverLicenseNumber;

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

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleCapacity() {
        return vehicleCapacity;
    }

    public void setVehicleCapacity(String vehicleCapacity) {
        this.vehicleCapacity = vehicleCapacity;
    }

    public String getVehicleOwnerName() {
        return vehicleOwnerName;
    }

    public void setVehicleOwnerName(String vehicleOwnerName) {
        this.vehicleOwnerName = vehicleOwnerName;
    }

    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
    }

    public String getAvailableArea() {
        return availableArea;
    }

    public void setAvailableArea(String availableArea) {
        this.availableArea = availableArea;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public void setVehicleDescription(String vehicleDescription) {
        this.vehicleDescription = vehicleDescription;
    }

    public Boolean getHasDriver() {
        return hasDriver;
    }

    public void setHasDriver(Boolean hasDriver) {
        this.hasDriver = hasDriver;
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

    public String getDriverDescription() {
        return driverDescription;
    }

    public void setDriverDescription(String driverDescription) {
        this.driverDescription = driverDescription;
    }
}