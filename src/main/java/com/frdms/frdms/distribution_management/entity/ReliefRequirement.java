package com.frdms.distribution_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "relief_requirements")
public class ReliefRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Relief item name is required")
    @Size(max = 100, message = "Relief item name cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String reliefItemName;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @NotBlank(message = "Unit is required")
    @Size(max = 30, message = "Unit cannot exceed 30 characters")
    @Column(nullable = false, length = 30)
    private String unit;

    @Size(max = 100, message = "Recipient name cannot exceed 100 characters")
    @Column(length = 100)
    private String recipientName;

    @Size(max = 150, message = "Affected area cannot exceed 150 characters")
    @Column(length = 150)
    private String affectedArea;

    @Size(max = 150, message = "Shelter cannot exceed 150 characters")
    @Column(length = 150)
    private String shelter;

    public ReliefRequirement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}