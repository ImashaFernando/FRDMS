package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.service.NecessaryItemsSummaryService;
import com.frdms.frdms.distribution_management.service.ResourceRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.frdms.frdms.distribution_management.service.NecessaryItemSummary;
import java.util.HashSet;
import java.util.Set;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        List<NecessaryItemSummary> necessaryItems =
                summaryService.getNecessaryItems();


        Set<String> requestedItemKeys = new HashSet<>();

        for (NecessaryItemSummary item : necessaryItems) {
            if (resourceRequestService.hasAlreadyBeenRequested(
                    item.getDisasterName(),
                    item.getItemName())) {

                requestedItemKeys.add(
                        item.getDisasterName() + "::" + item.getItemName()
                );
            }
        }

        model.addAttribute("requestedItemKeys", requestedItemKeys);


        Map<String, List<NecessaryItemSummary>> itemsByDisaster =
                new LinkedHashMap<>();

        for (NecessaryItemSummary item : necessaryItems) {

            itemsByDisaster
                    .computeIfAbsent(
                            item.getDisasterName(),
                            key -> new java.util.ArrayList<>()
                    )
                    .add(item);
        }

        model.addAttribute(
                "necessaryItems",
                necessaryItems
        );

        model.addAttribute(
                "itemsByDisaster",
                itemsByDisaster
        );

        return "distribution/necessary-items";
    }

    @PostMapping("/necessary-items/request")
    public String requestItem(
            @RequestParam String disasterName,
            @RequestParam String itemName,
            @RequestParam String quantity,
            @RequestParam String unit) {

        resourceRequestService.createRequest(
                disasterName,
                itemName,
                new java.math.BigDecimal(quantity),
                unit
        );

        return "redirect:/distribution/necessary-items";
    }
}