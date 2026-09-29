package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.service.InventoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/finance/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    // =========================================================
    // VIEW INVENTORY
    // =========================================================

    @GetMapping
    public String inventory(Model model) {

        model.addAttribute(
                "inventoryItems",
                inventoryService.getAllItems()
        );

        return "finance/inventory";
    }
}