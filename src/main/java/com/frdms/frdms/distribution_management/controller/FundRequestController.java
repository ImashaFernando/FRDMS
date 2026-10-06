package com.frdms.frdms.distribution_management.controller;

import com.frdms.frdms.distribution_management.entity.FundRequest;
import com.frdms.frdms.distribution_management.entity.ResourceRequest;
import com.frdms.frdms.distribution_management.service.FundRequestService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/distribution/fund-requests")
public class FundRequestController {

    private final FundRequestService fundRequestService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public FundRequestController(
            FundRequestService fundRequestService) {

        this.fundRequestService =
                fundRequestService;
    }


    // =========================================================
    // SHOW FUND REQUEST FORM
    // =========================================================

    @GetMapping("/new/{requestId}")
    public String showFundRequestForm(
            @PathVariable Long requestId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            ResourceRequest request =
                    fundRequestService
                            .getResourceRequest(
                                    requestId
                            );


            // -------------------------------------------------
            // ONLY LOW STOCK / NOT AVAILABLE
            // -------------------------------------------------

            if (!"LOW_STOCK".equalsIgnoreCase(
                    request.getStatus())
                    &&
                    !"NOT_AVAILABLE".equalsIgnoreCase(
                            request.getStatus())) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Fund request is only available " +
                                "for LOW STOCK or NOT AVAILABLE items."
                );

                return "redirect:/distribution/stock-status";
            }


            // -------------------------------------------------
            // CHECK DUPLICATE
            // -------------------------------------------------

            if (fundRequestService
                    .fundRequestExists(requestId)) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "A fund request has already been submitted."
                );

                return "redirect:/distribution/stock-status";
            }


            BigDecimal availableQuantity =
                    fundRequestService
                            .getAvailableQuantity(
                                    request
                            );


            BigDecimal requestedQuantity =
                    request.getQuantity();


            BigDecimal shortageQuantity =
                    requestedQuantity.subtract(
                            availableQuantity
                    );


            if (shortageQuantity.compareTo(
                    BigDecimal.ZERO) < 0) {

                shortageQuantity =
                        BigDecimal.ZERO;
            }


            model.addAttribute(
                    "request",
                    request
            );

            model.addAttribute(
                    "availableQuantity",
                    availableQuantity
            );

            model.addAttribute(
                    "shortageQuantity",
                    shortageQuantity
            );

            return "distribution/fund-request-form";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            return "redirect:/distribution/stock-status";
        }
    }


    // =========================================================
    // SUBMIT FUND REQUEST
    // =========================================================

    @PostMapping("/submit/{requestId}")
    public String submitFundRequest(
            @PathVariable Long requestId,

            @RequestParam(
                    "estimatedUnitPrice")
            BigDecimal estimatedUnitPrice,

            @RequestParam(
                    "priority")
            String priority,

            @RequestParam(
                    value = "notes",
                    required = false)
            String notes,

            RedirectAttributes redirectAttributes) {

        try {

            fundRequestService.createFundRequest(
                    requestId,
                    estimatedUnitPrice,
                    priority,
                    notes
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Fund request submitted successfully."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }


        return "redirect:/distribution/stock-status";
    }
}