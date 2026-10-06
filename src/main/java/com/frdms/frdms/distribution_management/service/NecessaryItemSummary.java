package com.frdms.frdms.distribution_management.service;

import java.math.BigDecimal;

public class NecessaryItemSummary {

    private String disasterName;
    private String itemName;
    private BigDecimal totalRequired;
    private String unit;

    public NecessaryItemSummary(
            String disasterName,
            String itemName,
            BigDecimal totalRequired,
            String unit) {

        this.disasterName = disasterName;
        this.itemName = itemName;
        this.totalRequired = totalRequired;
        this.unit = unit;
    }

    public String getDisasterName() {
        return disasterName;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getTotalRequired() {
        return totalRequired;
    }

    public String getUnit() {
        return unit;
    }
}