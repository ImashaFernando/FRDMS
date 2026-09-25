package com.frdms.frdms.victim_management.entity;

import com.frdms.frdms.disaster_management.entity.Disaster;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "NIC number is required")
    @Column(name = "nic_number", nullable = false, unique = true)
    private String nicNumber;

    /*
     * Username is automatically generated from the NIC number.
     * The victim does not need to enter a separate username.
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    /*
     * Disaster selected by the victim during registration.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disaster_id")
    private Disaster disaster;


    // ---------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------

    public Victim() {
    }

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


    // ---------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getNicNumber() {
        return nicNumber;
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


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }


    public Disaster getDisaster() {
        return disaster;
    }

    public void setDisaster(Disaster disaster) {
        this.disaster = disaster;
    }


    // ---------------------------------------------------------
    // Automatically prepare data before saving
    // ---------------------------------------------------------

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