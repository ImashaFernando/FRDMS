package com.frdms.frdms.donation_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Donor is required")
    private Long donorId;

    private String donationType;

    private Double cashAmount;

    private String goodsDescription;

    private String goodsQuantity;

    @NotBlank(message = "Description is required")
    private String description;

    private String status;

    // =========================
    // Donation Date
    // =========================

    @NotNull(message = "Donation date is required")
    @Column(nullable = false)
    private LocalDate donationDate;


    public Donation() {
    }


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


    // =========================
    // Donation Date Getter/Setter
    // =========================

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }
}