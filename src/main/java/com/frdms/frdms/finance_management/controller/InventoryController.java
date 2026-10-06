package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.distribution_management.entity.ResourceRequest;
import com.frdms.frdms.finance_management.service.InventoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/finance/inventory")
public class InventoryController {

    private final InventoryService inventoryService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public InventoryController(
            InventoryService inventoryService) {

        this.inventoryService = inventoryService;
    }


    // =========================================================
    // INVENTORY PAGE
    // =========================================================

    @GetMapping
    public String inventory(Model model) {

        model.addAttribute(
                "inventoryItems",
                inventoryService.getAllItems()
        );

        return "finance/inventory";
    }


    // =========================================================
    // DISTRIBUTION REQUESTS
    // =========================================================

    @GetMapping("/requests")
    public String distributionRequests(Model model) {

        List<ResourceRequest> requests =
                inventoryService.getDistributionRequests();


        // -----------------------------------------------------
        // REQUESTS
        // -----------------------------------------------------

        model.addAttribute(
                "requests",
                requests
        );


        // -----------------------------------------------------
        // AVAILABLE QUANTITIES
        // -----------------------------------------------------

        model.addAttribute(
                "availableQuantities",
                inventoryService.getAvailableQuantities(
                        requests
                )
        );


        // -----------------------------------------------------
        // ORIGINAL INVENTORY UNITS
        // -----------------------------------------------------

        model.addAttribute(
                "inventoryUnits",
                inventoryService.getInventoryUnits(
                        requests
                )
        );


        return "finance/inventory-requests";
    }


    // =========================================================
    // SEND GOODS
    // =========================================================

    @PostMapping("/requests/{id}/send")
    public String sendGoods(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            inventoryService.sendRequestToDistribution(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Request processed successfully. " +
                            "Goods sent to Distribution."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }


        // Stay on Finance Distribution Requests page
        return "redirect:/finance/inventory/requests";
    }


    // =========================================================
    // NOT AVAILABLE
    // =========================================================

    @PostMapping("/requests/{id}/not-available")
    public String markNotAvailable(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            inventoryService.markRequestAsNotAvailable(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Request processed successfully. " +
                            "Status updated to NOT AVAILABLE."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }


        // Stay on Finance Distribution Requests page
        return "redirect:/finance/inventory/requests";
    }


    // =========================================================
    // LOW STOCK
    // =========================================================

    @PostMapping("/requests/{id}/low-stock")
    public String markLowStock(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            inventoryService.markRequestAsLowStock(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Request processed successfully. " +
                            "Status updated to LOW STOCK."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }


        // Stay on Finance Distribution Requests page
        return "redirect:/finance/inventory/requests";
    }

}