package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.distribution_management.entity.FundRequest;
import com.frdms.frdms.distribution_management.service.FundRequestService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/finance/fund-requests")
public class FundRequestFinanceController {

    private final FundRequestService fundRequestService;

    public FundRequestFinanceController(
            FundRequestService fundRequestService) {

        this.fundRequestService = fundRequestService;
    }

    // =========================================================
    // FINANCE - VIEW INCOMING FUND REQUESTS
    // =========================================================

    @GetMapping
    public String viewFundRequests(Model model) {

        List<FundRequest> fundRequests =
                fundRequestService.getPendingFundRequests();

        model.addAttribute(
                "fundRequests",
                fundRequests
        );

        return "finance/fund-requests";
    }
}
