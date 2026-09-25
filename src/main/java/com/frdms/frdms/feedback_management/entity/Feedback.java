package com.frdms.frdms.feedback_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 20, message = "Contact number cannot exceed 20 characters")
    @Pattern(
            regexp = "^$|^(0\\d{9}|\\+94\\d{9})$",
            message = "Enter a valid Sri Lankan phone number"
    )
    @Column(name = "contact_number", length = 20)
    private String contactNumber;
    @NotBlank(message = "Please select who you are")
    @Column(name = "feedback_type", nullable = false, length = 30)
    private String feedbackType;

    @NotBlank(message = "Please select a service or activity")
    @Column(name = "service", nullable = false, length = 100)
    private String service;

    @Size(max = 100, message = "Affected area cannot exceed 100 characters")
    @Column(name = "affected_area", length = 100)
    private String affectedArea;
    @Size(max = 100, message = "Shelter name cannot exceed 100 characters")
    @Column(name = "shelter", length = 100)
    private String shelter;

    @Column(name = "service_date")
    private LocalDate serviceDate;
    @Column(name = "assistance_provided", length = 20)
    private String assistanceProvided;

    @Size(max = 100, message = "Community area cannot exceed 100 characters")
    @Column(name = "community_area", length = 100)
    private String communityArea;

    @Size(max = 100, message = "Activity location cannot exceed 100 characters")
    @Column(name = "activity_location", length = 100)
    private String activityLocation;

    @Column(name = "people_reached", length = 20)
    private String peopleReached;

    @Size(max = 1000, message = "Suggestions cannot exceed 1000 characters")
    @Column(name = "suggestions", length = 1000)
    private String suggestions;

    @Size(max = 100, message = "Service area cannot exceed 100 characters")
    @Column(name = "service_area", length = 100)
    private String serviceArea;

    @Size(max = 100, message = "Shelter or relief centre cannot exceed 100 characters")
    @Column(name = "service_location", length = 100)
    private String serviceLocation;

    @Column(name = "resources_available", length = 20)
    private String resourcesAvailable;

    @Column(name = "coordination_rating", length = 20)
    private String coordinationRating;

    @Size(max = 1000, message = "Difficulties cannot exceed 1000 characters")
    @Column(name = "difficulties", length = 1000)
    private String difficulties;
    @NotNull(message = "Please give a rating")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @NotBlank(message = "Please enter your feedback")
    @Size(max = 1000, message = "Feedback cannot exceed 1000 characters")
    @Column(name = "comments", nullable = false, length = 1000)
    private String comments;

    @Column(name = "feedback_date", nullable = false)
    private LocalDate feedbackDate = LocalDate.now();


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDate getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(LocalDate feedbackDate) {
        this.feedbackDate = feedbackDate;
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

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getAssistanceProvided() {
        return assistanceProvided;
    }

    public void setAssistanceProvided(String assistanceProvided) {
        this.assistanceProvided = assistanceProvided;
    }
    public String getCommunityArea() {
        return communityArea;
    }

    public void setCommunityArea(String communityArea) {
        this.communityArea = communityArea;
    }

    public String getActivityLocation() {
        return activityLocation;
    }

    public void setActivityLocation(String activityLocation) {
        this.activityLocation = activityLocation;
    }

    public String getPeopleReached() {
        return peopleReached;
    }

    public void setPeopleReached(String peopleReached) {
        this.peopleReached = peopleReached;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }
    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public String getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(String serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public String getResourcesAvailable() {
        return resourcesAvailable;
    }

    public void setResourcesAvailable(String resourcesAvailable) {
        this.resourcesAvailable = resourcesAvailable;
    }

    public String getCoordinationRating() {
        return coordinationRating;
    }

    public void setCoordinationRating(String coordinationRating) {
        this.coordinationRating = coordinationRating;
    }

    public String getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(String difficulties) {
        this.difficulties = difficulties;
    }

}