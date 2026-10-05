package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.entity.ResourceRequest;
import com.frdms.frdms.distribution_management.repository.ResourceRequestRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/distribution")
public class StockStatusController {

    private final ResourceRequestRepository repository;

    public StockStatusController(
            ResourceRequestRepository repository) {

        this.repository = repository;
    }

    @GetMapping("/stock-status")
    public String showStockStatus(Model model) {

        List<ResourceRequest> requests =
                repository.findAll();

        model.addAttribute("requests", requests);

        return "distribution/relief-supply-status";
    }
}