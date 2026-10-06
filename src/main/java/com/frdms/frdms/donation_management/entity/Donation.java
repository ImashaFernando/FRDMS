package com.frdms.frdms.donation_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // DONOR
    // =========================================================

    @NotNull(message = "Donor is required")
    private Long donorId;


    // =========================================================
    // DONATION TYPE
    // =========================================================

    @NotBlank(message = "Donation type is required")
    private String donationType;


    // =========================================================
    // CASH AMOUNT
    // =========================================================

    @DecimalMin(
            value = "0.01",
            message = "Cash amount must be greater than 0"
    )
    private Double cashAmount;


    // =========================================================
    // GOODS DESCRIPTION
    // =========================================================

    private String goodsDescription;


    // =========================================================
    // GOODS QUANTITY
    // =========================================================

    private String goodsQuantity;


    // =========================================================
    // DESCRIPTION
    // =========================================================

    private String description;


    // =========================================================
    // STATUS
    // =========================================================

    private String status;


    // =========================================================
    // DONATION DATE
    // =========================================================

    @NotNull(message = "Donation date is required")
    @Column(nullable = false)
    private LocalDate donationDate;


    // =========================================================
    // DONATION ITEMS
    // =========================================================
    // One Donation can contain many DonationItems
    // Example:
    //
    // Donation #1
    //   ├── Rice     - 10 kg
    //   ├── Dhal     - 5 kg
    //   └── Soap     - 20 pcs
    //
    // =========================================================

    @OneToMany(
            mappedBy = "donation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DonationItem> donationItems = new ArrayList<>();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Donation() {
    }


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }


    public String getDonationType() {
        return donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }


    public Double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Double cashAmount) {
        this.cashAmount = cashAmount;
    }


    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }


    public String getGoodsQuantity() {
        return goodsQuantity;
    }

    public void setGoodsQuantity(String goodsQuantity) {
        this.goodsQuantity = goodsQuantity;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }


    // =========================================================
    // DONATION ITEMS GETTER / SETTER
    // =========================================================

    public List<DonationItem> getDonationItems() {
        return donationItems;
    }

    public void setDonationItems(List<DonationItem> donationItems) {
        this.donationItems = donationItems;
    }
}