
package com.frdms.frdms.finance_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Column(nullable = false)
    private String itemName;

    private String category;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Quantity cannot be negative"
    )
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "minimum_stock", precision = 12, scale = 2)
    private BigDecimal minimumStock = BigDecimal.TEN;

    private String unit;

    private String description;

    public Inventory() {
    }

    public String getStockStatus() {
        if (quantity == null ||
                quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return "OUT_OF_STOCK";
        }

        if (minimumStock != null &&
                quantity.compareTo(minimumStock) <= 0) {
            return "LOW_STOCK";
        }

        return "AVAILABLE";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(BigDecimal minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
