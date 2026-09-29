package com.frdms.frdms.finance_management.repository;

import com.frdms.frdms.finance_management.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByItemNameIgnoreCase(String itemName);
}