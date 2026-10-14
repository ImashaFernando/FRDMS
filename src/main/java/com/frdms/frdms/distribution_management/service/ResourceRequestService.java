
package com.frdms.frdms.distribution_management.service;

import com.frdms.frdms.distribution_management.entity.ResourceRequest;
import com.frdms.frdms.distribution_management.repository.ResourceRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ResourceRequestService {

    private final ResourceRequestRepository repository;

    public ResourceRequestService(
            ResourceRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ResourceRequest createRequest(
            String disasterName,
            String itemName,
            BigDecimal quantity,
            String unit) {

        if (repository.existsByDisasterNameAndItemName(
                disasterName, itemName)) {
            return null;
        }

        ResourceRequest request = new ResourceRequest();
        request.setDisasterName(disasterName);
        request.setItemName(itemName);
        request.setQuantity(quantity);
        request.setUnit(unit);
        request.setStatus("REQUESTED");

        return repository.save(request);
    }

    public boolean hasAlreadyBeenRequested(
            String disasterName,
            String itemName) {

        return repository.existsByDisasterNameAndItemName(
                disasterName, itemName);
    }
}
