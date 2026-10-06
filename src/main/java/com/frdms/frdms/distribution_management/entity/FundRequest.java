package com.frdms.frdms.distribution_management.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "fund_requests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_fund_request_resource",
                        columnNames = "resource_request_id"
                )
        }
)
public class FundRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // ORIGINAL DISTRIBUTION REQUEST
    // =========================================================

    @Column(name = "resource_request_id",
            nullable = false,
            unique = true)
    private Long resourceRequestId;


    // =========================================================
    // REQUEST INFORMATION
    // =========================================================

    @Column(name = "disaster_name")
    private String disasterName;

    @Column(name = "item_name",
            nullable = false)
    private String itemName;

    @Column(name = "requested_quantity",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal requestedQuantity;

    @Column(name = "available_quantity",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal availableQuantity;

    @Column(name = "shortage_quantity",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal shortageQuantity;

    @Column(name = "unit")
    private String unit;


    // =========================================================
    // FUND REQUEST TYPE
    // =========================================================

    @Column(name = "request_type",
            nullable = false)
    private String requestType;

    /*
       LOW_STOCK
       NOT_AVAILABLE
    */


    // =========================================================
    // PURPOSE
    // =========================================================

    @Column(name = "purpose",
            length = 1000)
    private String purpose;


    // =========================================================
    // FINANCIAL INFORMATION
    // =========================================================

    @Column(name = "estimated_unit_price",
            precision = 12,
            scale = 2)
    private BigDecimal estimatedUnitPrice;

    @Column(name = "estimated_total_cost",
            precision = 12,
            scale = 2)
    private BigDecimal estimatedTotalCost;


    // =========================================================
    // PRIORITY
    // =========================================================

    @Column(name = "priority")
    private String priority;


    // =========================================================
    // ADDITIONAL NOTES
    // =========================================================

    @Column(name = "notes",
            length = 1000)
    private String notes;


    // =========================================================
    // STATUS
    // =========================================================

    @Column(name = "status",
            nullable = false)
    private String status = "FUND_REQUESTED";

    /*
       FUND_REQUESTED
       APPROVED
       REJECTED
       COMPLETED
    */


    // =========================================================
    // CREATED DATE
    // =========================================================

    @Column(name = "created_at",
            nullable = false)
    private LocalDateTime createdAt;


    @PrePersist
    public void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null || status.isBlank()) {
            status = "FUND_REQUESTED";
        }
    }


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public Long getResourceRequestId() {
        return resourceRequestId;
    }

    public void setResourceRequestId(Long resourceRequestId) {
        this.resourceRequestId = resourceRequestId;
    }

    public String getDisasterName() {
        return disasterName;
    }

    public void setDisasterName(String disasterName) {
        this.disasterName = disasterName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(
            BigDecimal requestedQuantity) {

        this.requestedQuantity = requestedQuantity;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(
            BigDecimal availableQuantity) {

        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getShortageQuantity() {
        return shortageQuantity;
    }

    public void setShortageQuantity(
            BigDecimal shortageQuantity) {

        this.shortageQuantity = shortageQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public BigDecimal getEstimatedUnitPrice() {
        return estimatedUnitPrice;
    }

    public void setEstimatedUnitPrice(
            BigDecimal estimatedUnitPrice) {

        this.estimatedUnitPrice = estimatedUnitPrice;
    }

    public BigDecimal getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public void setEstimatedTotalCost(
            BigDecimal estimatedTotalCost) {

        this.estimatedTotalCost = estimatedTotalCost;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}