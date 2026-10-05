package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.service.NecessaryItemsSummaryService;
import com.frdms.frdms.distribution_management.service.ResourceRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/distribution")
public class NecessaryItemsController {

    private final NecessaryItemsSummaryService summaryService;
    private final ResourceRequestService resourceRequestService;

    public NecessaryItemsController(
            NecessaryItemsSummaryService summaryService,
            ResourceRequestService resourceRequestService) {

        this.summaryService = summaryService;
        this.resourceRequestService = resourceRequestService;
    }

    @GetMapping("/necessary-items")
    public String showNecessaryItems(Model model) {

        model.addAttribute(
                "necessaryItems",
                summaryService.getNecessaryItems()
        );

        return "distribution/necessary-items";
    }

    @PostMapping("/necessary-items/request")
    public String requestItem(
            @RequestParam String itemName,
            @RequestParam String quantity,
            @RequestParam String unit) {

        resourceRequestService.createRequest(
                itemName,
                new java.math.BigDecimal(quantity),
                unit
        );

        return "redirect:/distribution/stock-status";
    }
}