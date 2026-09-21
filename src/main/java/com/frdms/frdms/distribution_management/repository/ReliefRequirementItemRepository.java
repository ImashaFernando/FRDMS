package com.frdms.frdms.distribution_management.repository;

import com.frdms.frdms.distribution_management.entity.ReliefRequirementItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReliefRequirementItemRepository
        extends JpaRepository<ReliefRequirementItem, Long> {

    List<ReliefRequirementItem> findByReliefItemNameIgnoreCase(String reliefItemName);
}