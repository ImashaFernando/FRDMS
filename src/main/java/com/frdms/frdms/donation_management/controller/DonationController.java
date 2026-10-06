package com.frdms.frdms.donation_management.controller;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.entity.DonationItem;
import com.frdms.frdms.donation_management.entity.Donor;
import com.frdms.frdms.donation_management.repository.DonorRepository;
import com.frdms.frdms.donation_management.service.DonationService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;
    private final DonorRepository donorRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DonationController(
            DonationService donationService,
            DonorRepository donorRepository) {

        this.donationService = donationService;
        this.donorRepository = donorRepository;
    }


    // =========================================================
    // DONOR - SUBMIT DONATION
    // =========================================================

    @GetMapping("/submit")
    public String showDonationForm(
            Authentication authentication,
            Model model) {

        Donor donor =
                getLoggedInDonor(authentication);

        Donation donation =
                new Donation();

        // Set logged-in donor automatically
        donation.setDonorId(donor.getId());

        model.addAttribute(
                "donation",
                donation
        );

        return "donation/donation-submit";
    }


    // =========================================================
    // DONOR - SUBMIT DONATION
    // =========================================================

    @PostMapping("/submit")
    public String submitDonation(

            @Valid
            @ModelAttribute("donation")
            Donation donation,

            BindingResult bindingResult,

            Authentication authentication,


            // =====================================================
            // NORMAL CHECKBOX ITEMS
            // =====================================================

            @RequestParam(
                    value = "selectedItems",
                    required = false
            )
            List<String> selectedItems,


            @RequestParam(
                    value = "itemQuantities",
                    required = false
            )
            List<BigDecimal> itemQuantities,


            @RequestParam(
                    value = "itemUnits",
                    required = false
            )
            List<String> itemUnits,


            // =====================================================
            // ADDITIONAL / SPECIAL ITEM
            // =====================================================

            @RequestParam(
                    value = "additionalItemName",
                    required = false
            )
            String additionalItemName,


            @RequestParam(
                    value = "additionalItemQuantity",
                    required = false
            )
            BigDecimal additionalItemQuantity,


            @RequestParam(
                    value = "additionalItemUnit",
                    required = false
            )
            String additionalItemUnit) {


        // =====================================================
        // GOODS DONATION VALIDATION
        // =====================================================

        if ("GOODS".equalsIgnoreCase(
                donation.getDonationType())) {

            boolean hasNormalItems =
                    selectedItems != null
                            && !selectedItems.isEmpty();

            boolean hasAdditionalItem =
                    additionalItemName != null
                            && !additionalItemName
                            .trim()
                            .isEmpty();


            // -------------------------------------------------
            // AT LEAST ONE ITEM
            // -------------------------------------------------

            if (!hasNormalItems &&
                    !hasAdditionalItem) {

                bindingResult.reject(
                        "items.required",
                        "Please select at least one item or add an additional donation item"
                );
            }


            // -------------------------------------------------
            // NORMAL ITEM VALIDATION
            // -------------------------------------------------

            if (hasNormalItems) {

                if (itemQuantities == null ||
                        itemQuantities.isEmpty()) {

                    bindingResult.reject(
                            "itemQuantities.required",
                            "Please enter quantities for the selected items"
                    );

                } else {

                    for (BigDecimal quantity :
                            itemQuantities) {

                        if (quantity == null ||
                                quantity.compareTo(
                                        BigDecimal.ZERO
                                ) <= 0) {

                            bindingResult.reject(
                                    "itemQuantities.invalid",
                                    "All donation quantities must be greater than zero"
                            );

                            break;
                        }
                    }
                }


                // -------------------------------------------------
                // ITEM / QUANTITY SIZE CHECK
                // -------------------------------------------------

                if (itemQuantities != null &&
                        selectedItems.size()
                                != itemQuantities.size()) {

                    bindingResult.reject(
                            "items.mismatch",
                            "Invalid donation item information"
                    );
                }


                // -------------------------------------------------
                // ITEM / UNIT SIZE CHECK
                // -------------------------------------------------

                if (itemUnits == null ||
                        itemUnits.isEmpty()) {

                    bindingResult.reject(
                            "units.required",
                            "Please select a unit for each donated item"
                    );

                } else if (
                        selectedItems.size()
                                != itemUnits.size()) {

                    bindingResult.reject(
                            "units.mismatch",
                            "Invalid donation unit information"
                    );
                }
            }


            // =================================================
            // ADDITIONAL ITEM VALIDATION
            // =================================================

            if (hasAdditionalItem) {

                if (additionalItemQuantity == null ||
                        additionalItemQuantity.compareTo(
                                BigDecimal.ZERO
                        ) <= 0) {

                    bindingResult.reject(
                            "additionalItemQuantity.invalid",
                            "Please enter a valid quantity for the additional item"
                    );
                }


                if (additionalItemUnit == null ||
                        additionalItemUnit
                                .trim()
                                .isEmpty()) {

                    bindingResult.reject(
                            "additionalItemUnit.required",
                            "Please select a unit for the additional item"
                    );
                }
            }
        }


        // =====================================================
        // CASH DONATION VALIDATION
        // =====================================================

        if ("CASH".equalsIgnoreCase(
                donation.getDonationType())) {

            if (donation.getCashAmount() == null) {

                bindingResult.rejectValue(
                        "cashAmount",
                        "cashAmount.required",
                        "Cash amount is required"
                );

            } else if (
                    donation.getCashAmount() <= 0) {

                bindingResult.rejectValue(
                        "cashAmount",
                        "cashAmount.invalid",
                        "Cash amount must be greater than 0"
                );
            }
        }


        // =====================================================
        // RETURN FORM IF VALIDATION ERRORS
        // =====================================================

        if (bindingResult.hasErrors()) {

            return "donation/donation-submit";
        }


        // =====================================================
        // GET LOGGED-IN DONOR
        // =====================================================

        Donor donor =
                getLoggedInDonor(authentication);


        // Never trust donorId coming from the form
        donation.setDonorId(
                donor.getId()
        );


        // =====================================================
        // CREATE DONATION ITEMS
        // =====================================================

        if ("GOODS".equalsIgnoreCase(
                donation.getDonationType())) {

            List<DonationItem> donationItems =
                    new ArrayList<>();


            // =================================================
            // ADD NORMAL CHECKBOX ITEMS
            // =================================================

            if (selectedItems != null &&
                    !selectedItems.isEmpty()) {

                for (int i = 0;
                     i < selectedItems.size();
                     i++) {

                    DonationItem item =
                            new DonationItem();


                    item.setDonation(
                            donation
                    );


                    item.setItemName(
                            selectedItems
                                    .get(i)
                    );


                    item.setQuantity(
                            itemQuantities.get(i)
                    );


                    item.setUnit(
                            itemUnits.get(i)
                    );


                    donationItems.add(item);
                }
            }


            // =================================================
            // ADDITIONAL / SPECIAL ITEM
            // =================================================

            if (additionalItemName != null &&
                    !additionalItemName
                            .trim()
                            .isEmpty()) {

                DonationItem additionalItem =
                        new DonationItem();


                additionalItem.setDonation(
                        donation
                );


                additionalItem.setItemName(
                        additionalItemName
                                .trim()
                );


                additionalItem.setQuantity(
                        additionalItemQuantity
                );


                additionalItem.setUnit(
                        additionalItemUnit
                );


                donationItems.add(
                        additionalItem
                );
            }


            // =================================================
            // ATTACH ITEMS TO DONATION
            // =================================================

            donation.setDonationItems(
                    donationItems
            );


            // =================================================
            // KEEP OLD FIELDS COMPATIBLE
            // =================================================

            StringBuilder descriptionText =
                    new StringBuilder();


            for (int i = 0;
                 i < donationItems.size();
                 i++) {

                if (i > 0) {

                    descriptionText.append(", ");
                }


                descriptionText.append(
                        donationItems
                                .get(i)
                                .getItemName()
                );
            }


            donation.setGoodsDescription(
                    descriptionText.toString()
            );


            StringBuilder quantityText =
                    new StringBuilder();


            for (int i = 0;
                 i < donationItems.size();
                 i++) {

                if (i > 0) {

                    quantityText.append(", ");
                }


                quantityText.append(
                        donationItems
                                .get(i)
                                .getQuantity()
                );


                quantityText.append(" ");


                quantityText.append(
                        donationItems
                                .get(i)
                                .getUnit()
                );
            }


            donation.setGoodsQuantity(
                    quantityText.toString()
            );
        }


        // =====================================================
        // SAVE DONATION
        // =====================================================

        donationService.submitDonation(
                donation
        );


        // =====================================================
        // DONATION SUCCESS PAGE
        // =====================================================

        return "redirect:/donations/success";
    }


    // =========================================================
    // DONOR - MY DONATIONS
    // =========================================================

    @GetMapping("/my")
    public String viewMyDonations(
            Authentication authentication,
            Model model) {

        Donor donor =
                getLoggedInDonor(authentication);


        List<Donation> donations =
                donationService.getDonationsByDonor(
                        donor.getId()
                );


        model.addAttribute(
                "donations",
                donations
        );


        model.addAttribute(
                "today",
                java.time.LocalDate.now()
        );


        return "donation/my-donations";
    }


    // =========================================================
    // DONOR - EDIT
    // =========================================================

    @GetMapping("/edit/{id}")
    public String showEditDonationForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Donor donor =
                getLoggedInDonor(authentication);


        Donation donation =
                donationService.getDonationById(id);


        // CHECK OWNERSHIP

        if (!donation.getDonorId()
                .equals(donor.getId())) {

            return "redirect:/donations/my";
        }


        // CHECK 2-DAY EDIT PERIOD

        if (!donationService
                .isWithinEditPeriod(donation)) {

            return "redirect:/donations/my";
        }


        model.addAttribute(
                "donation",
                donation
        );


        model.addAttribute(
                "donationItems",
                donation.getDonationItems()
        );


        return "donation/edit-donation";
    }


    // =========================================================
    // DONOR - UPDATE
    // =========================================================

    @PostMapping("/update/{id}")
    public String updateDonation(

            @PathVariable Long id,

            @Valid
            @ModelAttribute("donation")
            Donation donation,

            BindingResult bindingResult,

            Authentication authentication,

            Model model,


            @RequestParam(
                    value = "selectedItems",
                    required = false
            )
            List<String> selectedItems,


            @RequestParam(
                    value = "itemQuantities",
                    required = false
            )
            List<BigDecimal> itemQuantities,


            @RequestParam(
                    value = "itemUnits",
                    required = false
            )
            List<String> itemUnits,


            @RequestParam(
                    value = "additionalItemName",
                    required = false
            )
            String additionalItemName,


            @RequestParam(
                    value = "additionalItemQuantity",
                    required = false
            )
            BigDecimal additionalItemQuantity,


            @RequestParam(
                    value = "additionalItemUnit",
                    required = false
            )
            String additionalItemUnit) {


        // =====================================================
        // VALIDATION
        // =====================================================

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "donationItems",
                    donation.getDonationItems()
            );


            return "donation/edit-donation";
        }


        // =====================================================
        // GET LOGGED-IN DONOR
        // =====================================================

        Donor donor =
                getLoggedInDonor(authentication);


        try {

            donationService.updateDonation(

                    id,

                    donor.getId(),

                    donation,

                    selectedItems,

                    itemQuantities,

                    itemUnits,

                    additionalItemName,

                    additionalItemQuantity,

                    additionalItemUnit
            );


            return "redirect:/donations/my";


        } catch (IllegalStateException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );


            Donation existingDonation =
                    donationService.getDonationById(id);


            model.addAttribute(
                    "donation",
                    existingDonation
            );


            model.addAttribute(
                    "donationItems",
                    existingDonation.getDonationItems()
            );


            return "donation/edit-donation";
        }
    }


    // =========================================================
    // DONOR - DELETE
    // =========================================================

    @PostMapping("/delete/{id}")
    public String deleteDonation(
            @PathVariable Long id,
            Authentication authentication) {

        Donor donor =
                getLoggedInDonor(authentication);


        try {

            donationService.deleteDonation(
                    id,
                    donor.getId()
            );

        } catch (IllegalStateException e) {

            // If expired or not owned,
            // simply return to own donations
        }


        return "redirect:/donations/my";
    }


    // =========================================================
    // DONATION RECEIPT
    // =========================================================

    @GetMapping("/receipt/{id}")
    public String generateDonationReceipt(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Donor donor =
                getLoggedInDonor(authentication);


        Donation donation =
                donationService.getDonationById(id);


        if (!donation.getDonorId()
                .equals(donor.getId())) {

            return "redirect:/donations/my";
        }


        model.addAttribute(
                "donation",
                donation
        );


        return "donation/donation-receipt";
    }


    // =========================================================
    // SUCCESS
    // =========================================================

    @GetMapping("/success")
    public String donationSuccess() {

        return "donation/donation-success";
    }


    // =========================================================
    // GET LOGGED-IN DONOR
    // =========================================================

    private Donor getLoggedInDonor(
            Authentication authentication) {

        String username =
                authentication.getName();


        System.out.println(
                "======================================"
        );


        System.out.println(
                "LOGGED-IN USERNAME = " + username
        );


        System.out.println(
                "======================================"
        );


        return donorRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Logged-in donor not found. Username = "
                                        + username
                        )
                );
    }


    // =========================================================
    // FINANCE OFFICER - CASH DONATIONS
    // =========================================================

    @GetMapping("/cash")
    public String viewCashDonations(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "cashDonations",
                donationService.getCashDonations()
        );


        // Check logged-in user's role

        boolean isFinanceOfficer =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals(
                                                "ROLE_FINANCE_OFFICER"
                                        )
                        );


        model.addAttribute(
                "isFinanceOfficer",
                isFinanceOfficer
        );


        return "donation/cash-donations";
    }


    // =========================================================
    // FINANCE OFFICER - ACCEPTED CASH DONATIONS
    // =========================================================

    @GetMapping("/cash/accepted")
    public String viewAcceptedCashDonations(
            Authentication authentication,
            Model model) {

        List<Donation> acceptedCashDonations =
                donationService.getAcceptedCashDonations();


        // Calculate total accepted cash

        double totalAcceptedCash =
                acceptedCashDonations.stream()
                        .mapToDouble(donation ->
                                donation.getCashAmount() != null
                                        ? donation.getCashAmount()
                                        : 0.0
                        )
                        .sum();


        // Number of accepted donations

        int acceptedDonationCount =
                acceptedCashDonations.size();


        // Calculate average donation

        double averageAcceptedCash =
                acceptedDonationCount > 0
                        ? totalAcceptedCash /
                        acceptedDonationCount
                        : 0.0;


        model.addAttribute(
                "acceptedCashDonations",
                acceptedCashDonations
        );


        model.addAttribute(
                "totalAcceptedCash",
                totalAcceptedCash
        );


        model.addAttribute(
                "acceptedDonationCount",
                acceptedDonationCount
        );


        model.addAttribute(
                "averageAcceptedCash",
                averageAcceptedCash
        );


        boolean isFinanceOfficer =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals(
                                                "ROLE_FINANCE_OFFICER"
                                        )
                        );


        model.addAttribute(
                "isFinanceOfficer",
                isFinanceOfficer
        );


        return "donation/accepted-cash-donations";
    }


    // =========================================================
    // FINANCE OFFICER - GOODS DONATIONS
    // =========================================================

    @GetMapping("/goods")
    public String viewGoodsDonations(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "goodsDonations",
                donationService.getGoodsDonations()
        );


        boolean isFinanceOfficer =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals(
                                                "ROLE_FINANCE_OFFICER"
                                        )
                        );


        model.addAttribute(
                "isFinanceOfficer",
                isFinanceOfficer
        );


        return "donation/goods-donations";
    }


    // =========================================================
    // FINANCE OFFICER - ACCEPTED GOODS DONATIONS
    // =========================================================

    @GetMapping("/goods/accepted")
    public String acceptedGoodsDonations(
            Model model) {

        List<Donation> acceptedGoodsDonations =
                donationService.getAcceptedGoodsDonations();


        java.util.Map<Long, Donor> donors =
                new java.util.HashMap<>();


        for (Donation donation :
                acceptedGoodsDonations) {

            if (donation.getDonorId() != null) {

                donorRepository
                        .findById(
                                donation.getDonorId()
                        )
                        .ifPresent(donor ->
                                donors.put(
                                        donation.getDonorId(),
                                        donor
                                )
                        );
            }
        }


        int totalGoodsItemCount =
                acceptedGoodsDonations.stream()
                        .mapToInt(donation ->
                                donation.getDonationItems() != null
                                        ? donation
                                        .getDonationItems()
                                        .size()
                                        : 0
                        )
                        .sum();


        model.addAttribute(
                "acceptedGoodsDonations",
                acceptedGoodsDonations
        );


        model.addAttribute(
                "donors",
                donors
        );


        model.addAttribute(
                "totalGoodsItemCount",
                totalGoodsItemCount
        );


        return "donation/accepted-goods-donations";
    }


    // =========================================================
    // FINANCE OFFICER - ACCEPT DONATION
    // =========================================================

    @PostMapping("/accept/{id}")
    public String acceptDonation(
            @PathVariable Long id,
            @RequestParam String type) {

        donationService.acceptDonation(
                id
        );


        if ("GOODS".equalsIgnoreCase(type)) {

            return "redirect:/donations/goods";
        }


        return "redirect:/donations/cash";
    }


    // =========================================================
    // FINANCE OFFICER - REJECT DONATION
    // =========================================================

    @PostMapping("/reject/{id}")
    public String rejectDonation(
            @PathVariable Long id,
            @RequestParam String type) {

        donationService.rejectDonation(
                id
        );


        if ("GOODS".equalsIgnoreCase(type)) {

            return "redirect:/donations/goods";
        }


        return "redirect:/donations/cash";
    }
}