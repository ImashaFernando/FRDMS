package com.frdms.frdms.finance_management.service;

import com.frdms.frdms.distribution_management.entity.ResourceRequest;
import com.frdms.frdms.distribution_management.repository.ResourceRequestRepository;
import com.frdms.frdms.finance_management.entity.Inventory;
import com.frdms.frdms.finance_management.repository.InventoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ResourceRequestRepository resourceRequestRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public InventoryService(
            InventoryRepository inventoryRepository,
            ResourceRequestRepository resourceRequestRepository) {

        this.inventoryRepository = inventoryRepository;
        this.resourceRequestRepository = resourceRequestRepository;
    }


    // =========================================================
    // GET ALL INVENTORY ITEMS
    // =========================================================

    public List<Inventory> getAllItems() {

        return inventoryRepository.findAll();
    }


    // =========================================================
    // ADD DONATION TO INVENTORY
    // =========================================================

    public Inventory addDonationToInventory(
            String itemName,
            BigDecimal quantity,
            String unit,
            String description) {

        Inventory inventory =
                inventoryRepository
                        .findByItemNameIgnoreCase(itemName)
                        .orElse(null);


        // -----------------------------------------------------
        // ITEM DOES NOT EXIST
        // -----------------------------------------------------

        if (inventory == null) {

            inventory = new Inventory();

            inventory.setItemName(itemName);
            inventory.setQuantity(quantity);
            inventory.setUnit(unit);
            inventory.setCategory("Donated Goods");
            inventory.setDescription(description);
        }


        // -----------------------------------------------------
        // ITEM ALREADY EXISTS
        // -----------------------------------------------------

        else {

            BigDecimal currentQuantity =
                    inventory.getQuantity() == null
                            ? BigDecimal.ZERO
                            : inventory.getQuantity();

            inventory.setQuantity(
                    currentQuantity.add(quantity)
            );


            if (unit != null && !unit.isBlank()) {

                inventory.setUnit(unit);
            }


            if (description != null &&
                    !description.isBlank()) {

                inventory.setDescription(description);
            }
        }


        return inventoryRepository.save(inventory);
    }


    // =========================================================
    // SEND INVENTORY TO DISTRIBUTION
    // =========================================================

    public Inventory sendToDistribution(
            Long id,
            BigDecimal quantity) {

        Inventory inventory =
                inventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Inventory item not found"
                                )
                        );


        BigDecimal currentQuantity =
                inventory.getQuantity() == null
                        ? BigDecimal.ZERO
                        : inventory.getQuantity();


        // -----------------------------------------------------
        // VALIDATE QUANTITY
        // -----------------------------------------------------

        if (quantity == null ||
                quantity.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }


        if (quantity.compareTo(currentQuantity) > 0) {

            throw new IllegalArgumentException(
                    "Not enough inventory available"
            );
        }


        // -----------------------------------------------------
        // REDUCE INVENTORY
        // -----------------------------------------------------

        inventory.setQuantity(
                currentQuantity.subtract(quantity)
        );


        return inventoryRepository.save(inventory);
    }


    // =========================================================
    // GET DISTRIBUTION REQUESTS
    // =========================================================

    public List<ResourceRequest> getDistributionRequests() {

        return resourceRequestRepository.findAll();
    }


    // =========================================================
    // GET AVAILABLE QUANTITY
    // =========================================================

    public Map<String, BigDecimal> getAvailableQuantities(
            List<ResourceRequest> requests) {

        Map<String, BigDecimal> availableQuantities =
                new HashMap<>();


        for (ResourceRequest request : requests) {

            Inventory inventory =
                    inventoryRepository
                            .findByItemNameIgnoreCase(
                                    request.getItemName()
                            )
                            .orElse(null);


            if (inventory != null) {

                BigDecimal quantity =
                        inventory.getQuantity() == null
                                ? BigDecimal.ZERO
                                : inventory.getQuantity();

                availableQuantities.put(
                        request.getItemName(),
                        quantity
                );

            } else {

                availableQuantities.put(
                        request.getItemName(),
                        BigDecimal.ZERO
                );
            }
        }


        return availableQuantities;
    }


    // =========================================================
    // GET ORIGINAL INVENTORY UNITS
    // =========================================================

    public Map<String, String> getInventoryUnits(
            List<ResourceRequest> requests) {

        Map<String, String> inventoryUnits =
                new HashMap<>();


        for (ResourceRequest request : requests) {

            Inventory inventory =
                    inventoryRepository
                            .findByItemNameIgnoreCase(
                                    request.getItemName()
                            )
                            .orElse(null);


            if (inventory != null &&
                    inventory.getUnit() != null &&
                    !inventory.getUnit().isBlank()) {

                // Get original unit from Inventory
                inventoryUnits.put(
                        request.getItemName(),
                        inventory.getUnit()
                );

            } else {

                // Fallback to request unit
                inventoryUnits.put(
                        request.getItemName(),
                        request.getUnit()
                );
            }
        }


        return inventoryUnits;
    }


    // =========================================================
    // SEND REQUESTED GOODS TO DISTRIBUTION
    // =========================================================

    @Transactional
    public void sendRequestToDistribution(Long requestId) {

        ResourceRequest request =
                resourceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Resource request not found"
                                )
                        );


        // -----------------------------------------------------
        // CHECK REQUEST STATUS
        // -----------------------------------------------------

        if (!"REQUESTED".equalsIgnoreCase(
                request.getStatus())) {

            throw new IllegalStateException(
                    "This request has already been processed."
            );
        }


        // -----------------------------------------------------
        // CHECK REQUESTED QUANTITY
        // -----------------------------------------------------

        if (request.getQuantity() == null ||
                request.getQuantity()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Invalid requested quantity."
            );
        }


        // -----------------------------------------------------
        // FIND INVENTORY ITEM
        // -----------------------------------------------------

        Inventory inventory =
                inventoryRepository
                        .findByItemNameIgnoreCase(
                                request.getItemName()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Item '" +
                                                request.getItemName() +
                                                "' is not available in inventory."
                                )
                        );


        // -----------------------------------------------------
        // CHECK UNIT
        // -----------------------------------------------------

        String inventoryUnit =
                inventory.getUnit();

        String requestedUnit =
                request.getUnit();


        if (inventoryUnit != null &&
                requestedUnit != null &&
                !inventoryUnit.trim()
                        .equalsIgnoreCase(
                                requestedUnit.trim()
                        )) {

            throw new IllegalArgumentException(
                    "Unit mismatch. Inventory unit: " +
                            inventoryUnit +
                            ", requested unit: " +
                            requestedUnit
            );
        }


        // =====================================================
        // CHECK AVAILABLE STOCK
        // =====================================================

        BigDecimal requestedQuantity =
                request.getQuantity();

        BigDecimal availableQuantity =
                inventory.getQuantity() == null
                        ? BigDecimal.ZERO
                        : inventory.getQuantity();


        if (requestedQuantity.compareTo(
                availableQuantity) > 0) {

            throw new IllegalArgumentException(
                    "Not enough inventory available."
            );
        }


        // =====================================================
        // SEND GOODS
        // =====================================================

        sendToDistribution(
                inventory.getId(),
                requestedQuantity
        );


        // =====================================================
        // UPDATE REQUEST STATUS
        // =====================================================

        request.setStatus("SENT");

        resourceRequestRepository.save(request);
    }


    // =========================================================
    // MARK REQUEST AS NOT AVAILABLE
    // =========================================================

    @Transactional
    public void markRequestAsNotAvailable(Long requestId) {

        ResourceRequest request =
                resourceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Resource request not found"
                                )
                        );


        // -----------------------------------------------------
        // CHECK REQUEST STATUS
        // -----------------------------------------------------

        if (!"REQUESTED".equalsIgnoreCase(
                request.getStatus())) {

            throw new IllegalStateException(
                    "This request has already been processed."
            );
        }


        // -----------------------------------------------------
        // UPDATE STATUS
        // -----------------------------------------------------

        request.setStatus("NOT_AVAILABLE");

        resourceRequestRepository.save(request);
    }


    // =========================================================
    // MARK REQUEST AS LOW STOCK
    // =========================================================

    @Transactional
    public void markRequestAsLowStock(Long requestId) {

        ResourceRequest request =
                resourceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Resource request not found"
                                )
                        );


        // -----------------------------------------------------
        // CHECK REQUEST STATUS
        // -----------------------------------------------------

        if (!"REQUESTED".equalsIgnoreCase(
                request.getStatus())) {

            throw new IllegalStateException(
                    "This request has already been processed."
            );
        }


        // -----------------------------------------------------
        // UPDATE STATUS
        // -----------------------------------------------------

        request.setStatus("LOW_STOCK");

        resourceRequestRepository.save(request);
    }

}