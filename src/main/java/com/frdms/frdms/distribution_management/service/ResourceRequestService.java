package com.frdms.frdms.distribution_management.service;

import com.frdms.frdms.distribution_management.entity.ResourceRequest;
import com.frdms.frdms.distribution_management.repository.ResourceRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class ResourceRequestService {

    private final ResourceRequestRepository repository;

    public ResourceRequestService(
            ResourceRequestRepository repository) {
        this.repository = repository;
    }

    public ResourceRequest createRequest(
            String disasterName,
            String itemName,
            java.math.BigDecimal quantity,
            String unit) {

        ResourceRequest request = new ResourceRequest();

        request.setDisasterName(disasterName);
        request.setItemName(itemName);
        request.setQuantity(quantity);
        request.setUnit(unit);
        request.setStatus("REQUESTED");

        return repository.save(request);
    }
}