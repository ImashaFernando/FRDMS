package com.frdms.frdms.donation_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "donors")
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // USERNAME
    // =========================================================

    @NotBlank(message = "Username is required")
    @Size(
            min = 3,
            max = 30,
            message = "Username must be between 3 and 30 characters"
    )
    @Column(nullable = false, unique = true)
    private String username;


    // =========================================================
    // PASSWORD
    // =========================================================

    @NotBlank(message = "Password is required")
    @Size(
            min = 8,
            message = "Password must contain at least 8 characters"
    )
    @Column(nullable = false)
    private String password;


    // =========================================================
    // FULL NAME
    // =========================================================

    @NotBlank(message = "Full name is required")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Full name can contain only letters and spaces"
    )
    @Size(
            max = 100,
            message = "Full name cannot exceed 100 characters"
    )
    @Column(nullable = false)
    private String fullName;


    // =========================================================
    // CONTACT NUMBER
    // =========================================================

    @NotBlank(message = "Contact number is required")
    @Pattern(
            regexp = "^(07\\d{8}|\\+947\\d{8})$",
            message = "Enter a valid Sri Lankan mobile number"
    )
    @Column(nullable = false)
    private String contactNumber;


    // =========================================================
    // EMAIL
    // =========================================================

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Column(nullable = false)
    private String email;


    // =========================================================
    // ADDRESS
    // =========================================================

    @Size(
            max = 255,
            message = "Address cannot exceed 255 characters"
    )
    private String address;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Donor() {
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


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}