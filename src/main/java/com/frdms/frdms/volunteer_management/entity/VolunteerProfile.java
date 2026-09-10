package com.frdms.frdms.volunteer_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "volunteer_profiles")
public class VolunteerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "volunteer_account_id",
            nullable = false,
            unique = true
    )
    private VolunteerAccount volunteerAccount;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Volunteer must be at least 18 years old")
    @Max(value = 80, message = "Please enter a valid age")
    @Column(nullable = false)
    private Integer age;

    @NotBlank(message = "Work experience is required")
    @Size(
            max = 1000,
            message = "Work experience must not exceed 1000 characters"
    )
    @Column(nullable = false, length = 1000)
    private String workExperience;

    @NotBlank(message = "Skills are required")
    @Size(
            max = 500,
            message = "Skills must not exceed 500 characters"
    )
    @Column(nullable = false, length = 500)
    private String skills;

    @NotBlank(message = "Availability is required")
    @Column(nullable = false, length = 100)
    private String availability;

    @NotBlank(message = "Preferred working area is required")
    @Column(nullable = false, length = 150)
    private String preferredWorkingArea;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegistrationStatus registrationStatus = RegistrationStatus.PENDING;

    public VolunteerProfile() {
    }

    public VolunteerProfile(
            VolunteerAccount volunteerAccount,
            Integer age,
            String workExperience,
            String skills,
            String availability,
            String preferredWorkingArea
    ) {
        this.volunteerAccount = volunteerAccount;
        this.age = age;
        this.workExperience = workExperience;
        this.skills = skills;
        this.availability = availability;
        this.preferredWorkingArea = preferredWorkingArea;
        this.registrationStatus = RegistrationStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VolunteerAccount getVolunteerAccount() {
        return volunteerAccount;
    }

    public void setVolunteerAccount(VolunteerAccount volunteerAccount) {
        this.volunteerAccount = volunteerAccount;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getPreferredWorkingArea() {
        return preferredWorkingArea;
    }

    public void setPreferredWorkingArea(String preferredWorkingArea) {
        this.preferredWorkingArea = preferredWorkingArea;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public enum RegistrationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}