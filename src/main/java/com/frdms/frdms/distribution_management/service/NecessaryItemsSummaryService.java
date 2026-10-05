package com.frdms.frdms.distribution_management.service;

import com.frdms.frdms.distribution_management.entity.ReliefPackageItem;
import com.frdms.frdms.distribution_management.repository.ReliefPackageItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NecessaryItemsSummaryService {

    private final ReliefPackageItemRepository itemRepository;

    public NecessaryItemsSummaryService(
            ReliefPackageItemRepository itemRepository) {

        this.itemRepository = itemRepository;
    }

    public List<NecessaryItemSummary> getNecessaryItems() {

        List<ReliefPackageItem> items =
                itemRepository.findAllActivePackageItems();

        Map<String, NecessaryItemSummary> summaryMap =
                new LinkedHashMap<>();

        for (ReliefPackageItem item : items) {

            String disasterName =
                    item.getReliefPackage().getDisasterName();

            if (disasterName == null || disasterName.isBlank()) {
                disasterName = "Unknown Disaster";
            }

            String key =
                    disasterName.trim().toLowerCase()
                            + "|"
                            + item.getReliefItemName().trim().toLowerCase()
                            + "|"
                            + item.getUnit().trim().toLowerCase();

            NecessaryItemSummary existing =
                    summaryMap.get(key);

            if (existing == null) {

                summaryMap.put(
                        key,
                        new NecessaryItemSummary(
                                disasterName,
                                item.getReliefItemName(),
                                item.getQuantity(),
                                item.getUnit()
                        )
                );

            } else {

                BigDecimal newTotal =
                        existing.getTotalRequired()
                                .add(item.getQuantity());

                summaryMap.put(
                        key,
                        new NecessaryItemSummary(
                                existing.getDisasterName(),
                                existing.getItemName(),
                                newTotal,
                                existing.getUnit()
                        )
                );
            }
        }

        return new ArrayList<>(summaryMap.values());
    }
}