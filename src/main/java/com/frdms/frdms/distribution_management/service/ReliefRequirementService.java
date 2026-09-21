package com.frdms.frdms.distribution_management.service;


import com.frdms.frdms.distribution_management.entity.ReliefRequirement;
import com.frdms.frdms.distribution_management.entity.ReliefRequirementItem;
import com.frdms.frdms.distribution_management.repository.ReliefRequirementRepository;
import com.frdms.frdms.distribution_management.repository.ReliefRequirementItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ReliefRequirementService {

    private final ReliefRequirementRepository repository;
    private final ReliefRequirementItemRepository itemRepository;

    public ReliefRequirementService(
            ReliefRequirementRepository repository,
            ReliefRequirementItemRepository itemRepository) {

        this.repository = repository;
        this.itemRepository = itemRepository;
    }

    public ReliefRequirement saveRequirement(ReliefRequirement requirement) {

        // Connect every item to its parent requirement
        for (ReliefRequirementItem item : requirement.getItems()) {
            item.setRequirement(requirement);
        }

        // Save requirement and its items
        return repository.save(requirement);
    }

    public List<ReliefRequirement> getAllRequirements() {
        return repository.findAll();
    }
    public List<NecessaryItemSummary> getNecessaryItemSummary() {

        List<ReliefRequirementItem> items =
                itemRepository.findAll();

        Map<String, NecessaryItemSummary> summaryMap =
                new LinkedHashMap<>();

        for (ReliefRequirementItem item : items) {

            String key =
                    item.getReliefItemName().toLowerCase()
                            + "|" +
                            item.getUnit().toLowerCase();

            if (summaryMap.containsKey(key)) {

                NecessaryItemSummary existing =
                        summaryMap.get(key);

                existing.setTotalRequired(
                        existing.getTotalRequired()
                                .add(item.getQuantity())
                );

            } else {

                NecessaryItemSummary summary =
                        new NecessaryItemSummary(
                                item.getReliefItemName(),
                                item.getQuantity(),
                                item.getUnit()
                        );

                summaryMap.put(key, summary);
            }
        }

        return new ArrayList<>(summaryMap.values());
    }
}
