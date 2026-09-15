package com.frdms.frdms.victim_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Entity
@Table(name = "victims")
public class Victim {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // VICTIM NAME
    // =========================================================

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;


    // =========================================================
    // NIC NUMBER
    // Used for victim identification and verification
    // =========================================================

    @NotBlank(message = "NIC number is required")
    @Column(name = "nic_number", nullable = false, unique = true)
    private String nicNumber;


    // =========================================================
    // PHONE NUMBER
    // =========================================================

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain 10 digits"
    )
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;


    // =========================================================
    // ADDRESS
    // =========================================================

    @NotBlank(message = "Address is required")
    @Column(
            name = "address",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String address;


    // =========================================================
    // USERNAME
    // Used for victim login
    // =========================================================

    @NotBlank(message = "Username is required")
    @Column(name = "username", nullable = false, unique = true)
    private String username;


    // =========================================================
    // PASSWORD
    // Used for victim login
    // =========================================================

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;


    // =========================================================
    // REGISTRATION DATE
    // =========================================================

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Victim() {
    }


    public Victim(
            String fullName,
            String nicNumber,
            String phoneNumber,
            String address,
            String username,
            String password) {

        this.fullName = fullName;
        this.nicNumber = nicNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.username = username;
        this.password = password;
    }


    // =========================================================
    // PRE-PERSIST
    // =========================================================

    @PrePersist
    protected void onCreate() {

        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
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


    public String getPhoneNumber() {
        return phoneNumber;
    }


    public String getAddress() {
        return address;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}