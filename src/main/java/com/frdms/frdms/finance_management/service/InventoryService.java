package com.frdms.frdms.finance_management.service;

import com.frdms.frdms.finance_management.entity.Inventory;
import com.frdms.frdms.finance_management.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(
            InventoryRepository inventoryRepository) {

        this.inventoryRepository =
                inventoryRepository;
    }

    // =========================================================
    // GET ALL INVENTORY
    // =========================================================

    public List<Inventory> getAllItems() {

        return inventoryRepository.findAll();
    }

    // =========================================================
    // ADD ACCEPTED GOODS DONATION TO INVENTORY
    // =========================================================

    public Inventory addDonationToInventory(
            String itemName,
            int quantity,
            String description) {

        Inventory inventory =
                inventoryRepository
                        .findByItemNameIgnoreCase(itemName)
                        .orElse(null);

        // -----------------------------------------------------
        // NEW ITEM
        // -----------------------------------------------------

        if (inventory == null) {

            inventory = new Inventory();

            inventory.setItemName(itemName);
            inventory.setQuantity(quantity);
            inventory.setUnit("Units");
            inventory.setCategory("Donated Goods");
            inventory.setDescription(description);
        }

        // -----------------------------------------------------
        // EXISTING ITEM
        // -----------------------------------------------------

        else {

            int currentQuantity =
                    inventory.getQuantity() == null
                            ? 0
                            : inventory.getQuantity();

            inventory.setQuantity(
                    currentQuantity + quantity
            );
        }

        return inventoryRepository.save(inventory);
    }

    // =========================================================
    // SEND GOODS TO DISTRIBUTION MANAGER
    // =========================================================

    public Inventory sendToDistribution(
            Long id,
            int quantity) {

        Inventory inventory =
                inventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Inventory item not found"));

        int currentQuantity =
                inventory.getQuantity() == null
                        ? 0
                        : inventory.getQuantity();

        // -----------------------------------------------------
        // VALIDATE QUANTITY
        // -----------------------------------------------------

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than 0");
        }

        if (quantity > currentQuantity) {

            throw new IllegalArgumentException(
                    "Insufficient inventory quantity");
        }

        // -----------------------------------------------------
        // REDUCE INVENTORY
        // -----------------------------------------------------

        inventory.setQuantity(
                currentQuantity - quantity
        );

        return inventoryRepository.save(inventory);
    }
}