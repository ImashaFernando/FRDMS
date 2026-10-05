package com.frdms.frdms.distribution_management.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relief_packages")

public class ReliefPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_code", nullable = false, unique = true, length = 20)
    private String packageCode;

    @NotBlank(message = "Recipient name is required")
    @Size(min = 2, max = 100,
            message = "Recipient name must be between 2 and 100 characters")
    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @NotBlank(message = "Affected area is required")
    @Size(max = 100,
            message = "Affected area cannot exceed 100 characters")
    @Column(name = "affected_area", nullable = false, length = 100)
    private String affectedArea;

    @Size(max = 100,
            message = "Shelter name cannot exceed 100 characters")
    @Column(name = "shelter", length = 100)
    private String shelter;

    @NotBlank(message = "Delivery address is required")
    @Size(max = 255,
            message = "Delivery address cannot exceed 255 characters")
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Size(max = 20,
            message = "Contact number cannot exceed 20 characters")
    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @NotNull(message = "Package status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PackageStatus status = PackageStatus.REQUESTING_ITEMS;

    @Size(max = 20,
            message = "Vehicle number cannot exceed 20 characters")
    @Column(name = "vehicle_number", length = 20)
    private String vehicleNumber;

    @Size(max = 100,
            message = "Driver name cannot exceed 100 characters")
    @Column(name = "driver_name", length = 100)
    private String driverName;

    // =========================================================
// FAMILY COUNTS USED FOR PACKAGE QUANTITY CALCULATION
// These values are entered manually during package creation.
// They are not stored as database columns.
// =========================================================

    @Transient
    private Integer adults;

    @Transient
    private Integer children;

    @Transient
    private Integer women;

    @OneToMany(
            mappedBy = "reliefPackage",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Valid
    private List<ReliefPackageItem> items = new ArrayList<>();


    // Getters and Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getAffectedArea() {
        return affectedArea;
    }

    public void setAffectedArea(String affectedArea) {
        this.affectedArea = affectedArea;
    }

    public String getShelter() {
        return shelter;
    }

    public void setShelter(String shelter) {
        this.shelter = shelter;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Integer getAdults() {
        return adults;
    }

    public void setAdults(Integer adults) {
        this.adults = adults;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public Integer getWomen() {
        return women;
    }

    public void setWomen(Integer women) {
        this.women = women;
    }

    public List<ReliefPackageItem> getItems() {
        return items;
    }

    public void setItems(List<ReliefPackageItem> items) {
        this.items = items;
    }


    // Helper methods

    public void addItem(ReliefPackageItem item) {
        items.add(item);
        item.setReliefPackage(this);
    }

    public void removeItem(ReliefPackageItem item) {
        items.remove(item);
        item.setReliefPackage(null);
    }
}