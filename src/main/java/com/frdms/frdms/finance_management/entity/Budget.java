
package com.frdms.frdms.finance_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================================
    // DISASTER
    // =========================================================

    @NotBlank(message = "Disaster name is required")
    @Column(name = "disaster_name", nullable = false)
    private String disasterName;

    // =========================================================
    // ACTIVITY
    // =========================================================

    @NotBlank(message = "Activity is required")
    @Column(nullable = false)
    private String activity;

    // =========================================================
    // REQUESTED AMOUNT
    // =========================================================

    @NotNull(message = "Requested amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than 0"
    )
    @Column(nullable = false)
    private Double requestedAmount;

    // =========================================================
    // REASON
    // =========================================================

    @NotBlank(message = "Reason is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    // =========================================================
    // STATUS
    // =========================================================

    @Column(nullable = false)
    private String status;

    // =========================================================
    // ALLOCATED AMOUNT
    // =========================================================

    private Double allocatedAmount;

    // =========================================================
    // ALLOCATED DATE
    // =========================================================

    private LocalDate allocatedDate;

    // =========================================================
    // REJECTED DATE
    // =========================================================

    @Column(name = "rejected_date")
    private LocalDate rejectedDate;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Budget() {
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

    public String getDisasterName() {
        return disasterName;
    }

    public void setDisasterName(String disasterName) {
        this.disasterName = disasterName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(Double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public LocalDate getAllocatedDate() {
        return allocatedDate;
    }

    public void setAllocatedDate(LocalDate allocatedDate) {
        this.allocatedDate = allocatedDate;
    }

    public LocalDate getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(LocalDate rejectedDate) {
        this.rejectedDate = rejectedDate;
    }
}
