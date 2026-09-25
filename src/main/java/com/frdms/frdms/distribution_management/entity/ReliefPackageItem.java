package com.frdms.frdms.distribution_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "relief_package_items")
public class ReliefPackageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Relief item name is required")
    @Size(max = 100,
            message = "Relief item name cannot exceed 100 characters")
    @Column(name = "relief_item_name", nullable = false, length = 100)
    private String reliefItemName;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01",
            message = "Quantity must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @NotBlank(message = "Unit is required")
    @Size(max = 30,
            message = "Unit cannot exceed 30 characters")
    @Column(nullable = false, length = 30)
    private String unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private ReliefPackage reliefPackage;


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getReliefItemName() {
        return reliefItemName;
    }

    public void setReliefItemName(String reliefItemName) {
        this.reliefItemName = reliefItemName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ReliefPackage getReliefPackage() {
        return reliefPackage;
    }

    public void setReliefPackage(ReliefPackage reliefPackage) {
        this.reliefPackage = reliefPackage;
    }
}