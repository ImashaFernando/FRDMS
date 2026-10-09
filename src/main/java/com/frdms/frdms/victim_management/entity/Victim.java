package com.frdms.frdms.victim_management.entity;

import com.frdms.frdms.disaster_management.entity.Disaster;
import com.frdms.frdms.shelter_management.entity.Shelter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Entity
@Table(
        name = "victims",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "nic_number"),
                @UniqueConstraint(columnNames = "username")
        }
)
public class Victim {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // FULL NAME
    // =========================================================

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;


    // =========================================================
    // NIC NUMBER
    // =========================================================

    @NotBlank(message = "NIC number is required")
    @Pattern(
            regexp = "\\d{12}",
            message = "NIC number must contain exactly 12 digits"
    )
    @Column(
            name = "nic_number",
            nullable = false,
            unique = true
    )
    private String nicNumber;


    // =========================================================
    // USERNAME
    // NIC IS USED AS USERNAME
    // =========================================================

    @Column(
            name = "username",
            nullable = false,
            unique = true
    )
    private String username;


    // =========================================================
    // PASSWORD
    // =========================================================

    @NotBlank(message = "Password is required")
    @Column(
            name = "password",
            nullable = false
    )
    private String password;


    // =========================================================
    // PHONE NUMBER
    // =========================================================

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "\\d{10}",
            message = "Phone number must contain exactly 10 digits"
    )
    @Column(
            name = "phone_number",
            nullable = false
    )
    private String phoneNumber;


    // =========================================================
    // ADDRESS
    // =========================================================

    @NotBlank(message = "Address is required")
    @Column(
            name = "address",
            nullable = false
    )
    private String address;


    // =========================================================
    // REGISTRATION DATE
    // =========================================================

    @Column(name = "registration_date")
    private LocalDate registrationDate;


    // =========================================================
    // DISASTER / FLOOD EVENT
    // =========================================================

    /*
     * Each victim is associated with the flood event
     * for which assistance is requested.
     *
     * Many victims can belong to one disaster.
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disaster_id")
    private Disaster disaster;


    // =========================================================
    // AFFECTED AREA
    // =========================================================

    /*
     * Stores the specific affected area of this victim.
     *
     * This is different from the Disaster itself because
     * one disaster can affect multiple areas.
     */

    @Column(
            name = "affected_area",
            columnDefinition = "TEXT"
    )
    private String affectedArea;


    // =========================================================
    // SHELTER
    // =========================================================

    /*
     * A victim may be assigned to one shelter.
     *
     * Many victims can belong to the same shelter.
     *
     * If the victim is not staying in a shelter,
     * this value remains NULL.
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;


    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public Victim() {
    }


    /*
     * Existing constructor preserved.
     */
    public Victim(
            String fullName,
            String nicNumber,
            String password,
            String phoneNumber,
            String address,
            Disaster disaster
    ) {

        this.fullName = fullName;
        this.nicNumber = nicNumber;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.disaster = disaster;

        // NIC is used as the login username
        this.username = nicNumber;

        this.registrationDate = LocalDate.now();
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }


    public String getFullName() {
        return fullName;
    }


    public String getNicNumber() {
        return nicNumber;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }


    public String getAddress() {
        return address;
    }


    public LocalDate getRegistrationDate() {
        return registrationDate;
    }


    public Disaster getDisaster() {
        return disaster;
    }


    public String getAffectedArea() {
        return affectedArea;
    }


    public Shelter getShelter() {
        return shelter;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public void setNicNumber(String nicNumber) {

        this.nicNumber = nicNumber;

        /*
         * Automatically keep username equal to NIC.
         */
        if (nicNumber != null && !nicNumber.trim().isEmpty()) {
            this.username = nicNumber.trim();
        }
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }


    public void setDisaster(Disaster disaster) {
        this.disaster = disaster;
    }


    public void setAffectedArea(String affectedArea) {
        this.affectedArea = affectedArea;
    }


    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }


    // =========================================================
    // PRE-PERSIST
    // =========================================================

    @PrePersist
    protected void beforeSave() {

        /*
         * NIC is the victim's login identifier.
         */
        if (username == null || username.trim().isEmpty()) {
            username = nicNumber;
        }


        /*
         * Automatically store today's registration date.
         */
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
    }
}