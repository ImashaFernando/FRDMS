
package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.finance_management.service.FinancialAnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FinancialAnalyticsController {

    private final FinancialAnalyticsService analyticsService;

    public FinancialAnalyticsController(
            FinancialAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/finance/analytics")
    public String showAnalytics(Model model) {

        model.addAttribute(
                "monthlyDonationTrends",
                analyticsService.getMonthlyDonationTrends()
        );

        model.addAttribute(
                "donationStatusBreakdown",
                analyticsService.getDonationStatusBreakdown()
        );

        model.addAttribute(
                "budgetAllocationComparison",
                analyticsService.getBudgetAllocationComparison()
        );

        return "finance/analytics";
    }
}
