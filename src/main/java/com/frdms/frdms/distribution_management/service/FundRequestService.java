package com.frdms.frdms.distribution_management.service;

import com.frdms.frdms.distribution_management.entity.FundRequest;
import com.frdms.frdms.distribution_management.entity.ResourceRequest;
import com.frdms.frdms.distribution_management.repository.FundRequestRepository;
import com.frdms.frdms.distribution_management.repository.ResourceRequestRepository;
import com.frdms.frdms.finance_management.entity.Inventory;
import com.frdms.frdms.finance_management.repository.InventoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FundRequestService {

    private final FundRequestRepository fundRequestRepository;
    private final ResourceRequestRepository resourceRequestRepository;
    private final InventoryRepository inventoryRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public FundRequestService(
            FundRequestRepository fundRequestRepository,
            ResourceRequestRepository resourceRequestRepository,
            InventoryRepository inventoryRepository) {

        this.fundRequestRepository =
                fundRequestRepository;

        this.resourceRequestRepository =
                resourceRequestRepository;

        this.inventoryRepository =
                inventoryRepository;
    }


    // =========================================================
    // GET RESOURCE REQUEST
    // =========================================================

    public ResourceRequest getResourceRequest(
            Long requestId) {

        return resourceRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Resource request not found."
                        )
                );
    }


    // =========================================================
    // CHECK WHETHER FUND REQUEST EXISTS
    // =========================================================

    public boolean fundRequestExists(
            Long requestId) {

        return fundRequestRepository
                .existsByResourceRequestId(requestId);
    }


    // =========================================================
    // GET AVAILABLE QUANTITY
    // =========================================================

    public BigDecimal getAvailableQuantity(
            ResourceRequest request) {

        Inventory inventory =
                inventoryRepository
                        .findByItemNameIgnoreCase(
                                request.getItemName()
                        )
                        .orElse(null);

        if (inventory == null ||
                inventory.getQuantity() == null) {

            return BigDecimal.ZERO;
        }

        return inventory.getQuantity();
    }


    // =========================================================
    // CREATE FUND REQUEST
    // =========================================================

    @Transactional
    public FundRequest createFundRequest(
            Long resourceRequestId,
            BigDecimal estimatedUnitPrice,
            String priority,
            String notes) {

        ResourceRequest request =
                getResourceRequest(
                        resourceRequestId
                );


        // -----------------------------------------------------
        // ONLY LOW STOCK / NOT AVAILABLE
        // -----------------------------------------------------

        if (!"LOW_STOCK".equalsIgnoreCase(
                request.getStatus())
                &&
                !"NOT_AVAILABLE".equalsIgnoreCase(
                        request.getStatus())) {

            throw new IllegalStateException(
                    "Funds can only be requested for " +
                            "LOW_STOCK or NOT_AVAILABLE requests."
            );
        }


        // -----------------------------------------------------
        // PREVENT DUPLICATE REQUEST
        // -----------------------------------------------------

        if (fundRequestRepository
                .existsByResourceRequestId(
                        resourceRequestId)) {

            throw new IllegalStateException(
                    "A fund request has already been " +
                            "submitted for this request."
            );
        }


        // -----------------------------------------------------
        // QUANTITIES
        // -----------------------------------------------------

        BigDecimal requestedQuantity =
                request.getQuantity();

        if (requestedQuantity == null ||
                requestedQuantity.compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Invalid requested quantity."
            );
        }


        BigDecimal availableQuantity =
                getAvailableQuantity(request);


        BigDecimal shortageQuantity =
                requestedQuantity.subtract(
                        availableQuantity
                );


        if (shortageQuantity.compareTo(
                BigDecimal.ZERO) < 0) {

            shortageQuantity =
                    BigDecimal.ZERO;
        }


        // -----------------------------------------------------
        // VALIDATE SHORTAGE
        // -----------------------------------------------------

        if (shortageQuantity.compareTo(
                BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "There is no shortage for this request."
            );
        }


        // -----------------------------------------------------
        // UNIT PRICE
        // -----------------------------------------------------

        if (estimatedUnitPrice == null ||
                estimatedUnitPrice.compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Estimated unit price must be greater than zero."
            );
        }


        // -----------------------------------------------------
        // CALCULATE TOTAL COST
        // -----------------------------------------------------

        BigDecimal totalCost =
                shortageQuantity.multiply(
                        estimatedUnitPrice
                );


        // -----------------------------------------------------
        // CREATE FUND REQUEST
        // -----------------------------------------------------

        FundRequest fundRequest =
                new FundRequest();

        fundRequest.setResourceRequestId(
                resourceRequestId
        );

        fundRequest.setDisasterName(
                request.getDisasterName()
        );

        fundRequest.setItemName(
                request.getItemName()
        );

        fundRequest.setRequestedQuantity(
                requestedQuantity
        );

        fundRequest.setAvailableQuantity(
                availableQuantity
        );

        fundRequest.setShortageQuantity(
                shortageQuantity
        );

        fundRequest.setUnit(
                request.getUnit()
        );

        fundRequest.setRequestType(
                request.getStatus()
        );


        // -----------------------------------------------------
        // PURPOSE
        // -----------------------------------------------------

        String purpose =
                "Purchase " +
                        shortageQuantity.stripTrailingZeros()
                                .toPlainString() +
                        " " +
                        request.getUnit() +
                        " of " +
                        request.getItemName() +
                        " for relief distribution in " +
                        (request.getDisasterName() != null
                                ? request.getDisasterName()
                                : "the affected area") +
                        " because the available inventory is insufficient.";

        fundRequest.setPurpose(purpose);


        // -----------------------------------------------------
        // FINANCIAL DETAILS
        // -----------------------------------------------------

        fundRequest.setEstimatedUnitPrice(
                estimatedUnitPrice
        );

        fundRequest.setEstimatedTotalCost(
                totalCost
        );


        // -----------------------------------------------------
        // OTHER DETAILS
        // -----------------------------------------------------

        fundRequest.setPriority(priority);

        fundRequest.setNotes(notes);

        fundRequest.setStatus(
                "FUND_REQUESTED"
        );


        FundRequest saved =
                fundRequestRepository.save(
                        fundRequest
                );


        // -----------------------------------------------------
        // UPDATE ORIGINAL RESOURCE REQUEST
        // -----------------------------------------------------

        request.setStatus(
                "FUND_REQUESTED"
        );

        resourceRequestRepository.save(
                request
        );


        return saved;
    }
// =========================================================
// FINANCE - VIEW INCOMING FUND REQUESTS
// =========================================================

    public List<FundRequest> getPendingFundRequests() {

        return fundRequestRepository.findByStatus(
                "FUND_REQUESTED"
        );
    }


}