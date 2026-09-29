package com.frdms.frdms.distribution_management.service;

import com.frdms.frdms.distribution_management.entity.TransportResource;
import com.frdms.frdms.distribution_management.repository.TransportResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportResourceService {

    private final TransportResourceRepository transportResourceRepository;

    public TransportResourceService(TransportResourceRepository transportResourceRepository) {
        this.transportResourceRepository = transportResourceRepository;
    }

    public TransportResource saveTransportResource(TransportResource transportResource) {
        return transportResourceRepository.save(transportResource);
    }

    public List<TransportResource> getAllTransportResources() {
        return transportResourceRepository.findAll();
    }

    public Optional<TransportResource> getTransportResourceById(Long id) {
        return transportResourceRepository.findById(id);
    }

    public void deleteTransportResource(Long id) {
        transportResourceRepository.deleteById(id);
    }
}