package com.frdms.frdms.donation_management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "donation_items")
public class DonationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "donation_id",
            nullable = false
    )
    private Donation donation;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private Integer quantity;

    private String unit;


    // =====================================
    // CONSTRUCTOR
    // =====================================

    public DonationItem() {
    }


    // =====================================
    // GETTERS
    // =====================================

    public Long getId() {
        return id;
    }

    public Donation getDonation() {
        return donation;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }


    // =====================================
    // SETTERS
    // =====================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setDonation(
            Donation donation) {

        this.donation = donation;
    }

    public void setItemName(
            String itemName) {

        this.itemName = itemName;
    }

    public void setQuantity(
            Integer quantity) {

        this.quantity = quantity;
    }

    public void setUnit(
            String unit) {

        this.unit = unit;
    }
}