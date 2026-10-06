package com.frdms.frdms.finance_management.service;

import com.frdms.frdms.finance_management.entity.Inventory;
import com.frdms.frdms.finance_management.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public InventoryService(
            InventoryRepository inventoryRepository) {

        this.inventoryRepository = inventoryRepository;
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
            int quantity,
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

            int currentQuantity =
                    inventory.getQuantity() == null
                            ? 0
                            : inventory.getQuantity();

            inventory.setQuantity(
                    currentQuantity + quantity
            );


            // Update unit if provided
            if (unit != null && !unit.isBlank()) {

                inventory.setUnit(unit);
            }


            // Update description if provided
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
            int quantity) {

        Inventory inventory =
                inventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Inventory item not found"
                                )
                        );


        int currentQuantity =
                inventory.getQuantity() == null
                        ? 0
                        : inventory.getQuantity();


        // -----------------------------------------------------
        // VALIDATE QUANTITY
        // -----------------------------------------------------

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }


        if (quantity > currentQuantity) {

            throw new IllegalArgumentException(
                    "Not enough inventory available"
            );
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