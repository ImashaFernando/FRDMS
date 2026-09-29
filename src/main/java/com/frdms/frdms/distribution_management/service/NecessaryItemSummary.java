package com.frdms.frdms.distribution_management.service;

import java.math.BigDecimal;

public class NecessaryItemSummary {

    private String itemName;
    private BigDecimal totalRequired;
    private String unit;

    public NecessaryItemSummary(String itemName,
                                BigDecimal totalRequired,
                                String unit) {
        this.itemName = itemName;
        this.totalRequired = totalRequired;
        this.unit = unit;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getTotalRequired() {
        return totalRequired;
    }
    public void setTotalRequired(BigDecimal totalRequired) {
        this.totalRequired = totalRequired;
    }

    public String getUnit() {
        return unit;
    }
}