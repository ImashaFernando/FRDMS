package com.frdms.frdms.distribution_management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relief_requirements")
public class ReliefRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Recipient name is required")
    @Size(max = 100, message = "Recipient name cannot exceed 100 characters")
    @Column(length = 100)
    private String recipientName;

    @Size(max = 150, message = "Affected area cannot exceed 150 characters")
    @Column(length = 150)
    private String affectedArea;

    @Size(max = 150, message = "Shelter cannot exceed 150 characters")
    @Column(length = 150)
    private String shelter;

    @Size(max = 255, message = "Delivery address is too long")
    private String deliveryAddress;

    @Size(max = 15, message = "Contact number is too long")
    private String contactNumber;

    @Valid
    @NotEmpty(message = "At least one relief item is required")
    @OneToMany(
            mappedBy = "requirement",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ReliefRequirementItem> items = new ArrayList<>();


    // Default constructor
    public ReliefRequirement() {
    }


    // Getters and Setters

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<ReliefRequirementItem> getItems() {
        return items;
    }

    public void setItems(List<ReliefRequirementItem> items) {
        this.items = items;
    }


    // Helper method to add an item
    public void addItem(ReliefRequirementItem item) {
        items.add(item);
        item.setRequirement(this);
    }


    // Helper method to remove an item
    public void removeItem(ReliefRequirementItem item) {
        items.remove(item);
        item.setRequirement(null);
    }
}