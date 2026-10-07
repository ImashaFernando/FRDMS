package com.frdms.frdms.finance_management.controller;

import com.frdms.frdms.distribution_management.service.FundRequestService;
import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.service.BudgetService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/finance/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    private final FundRequestService fundRequestService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public BudgetController(
            BudgetService budgetService,
            FundRequestService fundRequestService) {

        this.budgetService = budgetService;

        this.fundRequestService =
                fundRequestService;
    }


    // =========================================================
    // CREATE BUDGET
    // =========================================================

    @GetMapping("/create")
    public String showCreateBudgetForm(
            Model model) {

        model.addAttribute(
                "budget",
                new Budget()
        );

        return "finance/create-budget";
    }


    @PostMapping("/create")
    public String createBudgetRequirement(
            @Valid @ModelAttribute("budget") Budget budget,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return "finance/create-budget";
        }

        budgetService.createBudgetRequirement(
                budget
        );

        return "redirect:/finance/budgets/success";
    }


    // =========================================================
    // VIEW ALL BUDGETS
    // =========================================================

    @GetMapping
    public String viewBudgets(
            Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getAllBudgets()
        );

        return "finance/budget-records";
    }


    // =========================================================
    // EDIT - SHOW FORM
    // ONLY PENDING BUDGETS
    // =========================================================

    @GetMapping("/edit/{id}")
    public String showEditBudgetForm(
            @PathVariable Long id,
            Model model) {

        try {

            Budget budget =
                    budgetService.getBudgetById(id);


            // Only PENDING can be edited
            if (!"PENDING".equalsIgnoreCase(
                    budget.getStatus())) {

                return "redirect:/finance/budgets/status";
            }


            model.addAttribute(
                    "budget",
                    budget
            );


            return "finance/edit-budget";

        } catch (IllegalArgumentException e) {

            return "redirect:/finance/budgets/status";
        }
    }


    // =========================================================
    // EDIT - UPDATE
    // ONLY REASON IS UPDATED
    // =========================================================

    @PostMapping("/edit/{id}")
    public String updateBudget(
            @PathVariable Long id,
            @RequestParam("reason") String reason,
            Model model) {

        try {

            budgetService.updatePendingBudgetReason(
                    id,
                    reason
            );


            // Return to budget status
            return "redirect:/finance/budgets/status";


        } catch (
                IllegalArgumentException |
                IllegalStateException e) {


            try {

                Budget budget =
                        budgetService.getBudgetById(id);

                model.addAttribute(
                        "budget",
                        budget
                );

            } catch (Exception ignored) {

                return "redirect:/finance/budgets/status";
            }


            model.addAttribute(
                    "error",
                    e.getMessage()
            );


            return "finance/edit-budget";
        }
    }


    // =========================================================
    // DELETE PENDING BUDGET
    // =========================================================

    @PostMapping("/delete/{id}")
    public String deleteBudget(
            @PathVariable Long id) {

        try {

            budgetService.deletePendingBudget(
                    id
            );

        } catch (
                IllegalArgumentException |
                IllegalStateException e) {

            // Return to status page
        }


        return "redirect:/finance/budgets/status";
    }


    // =========================================================
    // APPROVED BUDGETS
    // =========================================================

    @GetMapping("/approved")
    public String viewApprovedBudgets(
            Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getApprovedBudgets()
        );

        return "finance/allocate-budget";
    }


    // =========================================================
    // ALLOCATION FORM
    // =========================================================

    @GetMapping("/allocate/{id}")
    public String showAllocationForm(
            @PathVariable Long id,
            Model model) {

        Budget budget =
                budgetService.getBudgetById(id);


        if (!"APPROVED".equals(
                budget.getStatus())) {

            return "redirect:/finance/budgets/approved";
        }


        model.addAttribute(
                "budget",
                budget
        );


        return "finance/allocate-budget-form";
    }


    // =========================================================
    // ALLOCATE BUDGET
    // =========================================================

    @PostMapping("/allocate/{id}")
    public String allocateBudget(
            @PathVariable Long id,
            @RequestParam("allocatedAmount")
            Double allocatedAmount,
            Model model) {

        try {

            budgetService.allocateBudget(
                    id,
                    allocatedAmount
            );


            return "redirect:/finance/budgets/status";


        } catch (
                IllegalArgumentException |
                IllegalStateException e) {


            Budget budget =
                    budgetService.getBudgetById(id);


            model.addAttribute(
                    "budget",
                    budget
            );


            model.addAttribute(
                    "error",
                    e.getMessage()
            );


            return "finance/allocate-budget-form";
        }
    }


    // =========================================================
    // BUDGET STATUS
    // =========================================================

    @GetMapping("/status")
    public String viewBudgetStatus(
            Model model) {

        List<Budget> budgets =
                budgetService.getAllBudgets();


        long pendingCount =
                budgets.stream()
                        .filter(b ->
                                "PENDING".equalsIgnoreCase(
                                        b.getStatus()))
                        .count();


        long approvedCount =
                budgets.stream()
                        .filter(b ->
                                "APPROVED".equalsIgnoreCase(
                                        b.getStatus()))
                        .count();


        long allocatedCount =
                budgets.stream()
                        .filter(b ->
                                "ALLOCATED".equalsIgnoreCase(
                                        b.getStatus()))
                        .count();


        long rejectedCount =
                budgets.stream()
                        .filter(b ->
                                "REJECTED".equalsIgnoreCase(
                                        b.getStatus()))
                        .count();


        model.addAttribute(
                "budgets",
                budgets
        );


        model.addAttribute(
                "pendingCount",
                pendingCount
        );


        model.addAttribute(
                "approvedCount",
                approvedCount
        );


        model.addAttribute(
                "allocatedCount",
                allocatedCount
        );


        model.addAttribute(
                "rejectedCount",
                rejectedCount
        );


        return "finance/budget-status";
    }


    // =========================================================
    // REJECTED / TRASH
    // =========================================================

    @GetMapping("/trash")
    public String viewRejectedBudgets(
            Model model) {

        model.addAttribute(
                "budgets",
                budgetService.getRejectedBudgets()
        );

        return "finance/budget-trash";
    }


    // =========================================================
    // SUCCESS
    // =========================================================

    @GetMapping("/success")
    public String budgetSuccess() {

        return "finance/budget-success";
    }


    // =========================================================
    // CREATE BUDGET FROM FUND REQUESTS
    // =========================================================

    @PostMapping("/create-from-fund-requests")
    public String createBudgetFromFundRequests(
            @RequestParam("fundRequestIds")
            List<Long> fundRequestIds,
            Model model) {

        try {

            budgetService.createBudgetFromFundRequests(
                    fundRequestIds
            );


            return "redirect:/finance/budgets/status";


        } catch (
                IllegalArgumentException |
                IllegalStateException e) {


            model.addAttribute(
                    "error",
                    e.getMessage()
            );


            model.addAttribute(
                    "fundRequests",
                    fundRequestService
                            .getPendingFundRequests()
            );


            return "finance/fund-requests";
        }
    }

}