package com.frdms.distribution_management.service;

import com.frdms.distribution_management.entity.ReliefRequirement;
import com.frdms.distribution_management.repository.ReliefRequirementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReliefRequirementService {

    private final ReliefRequirementRepository repository;

    public ReliefRequirementService(ReliefRequirementRepository repository) {
        this.repository = repository;
    }

    public ReliefRequirement saveRequirement(ReliefRequirement requirement) {
        return repository.save(requirement);
    }

    public List<ReliefRequirement> getAllRequirements() {
        return repository.findAll();
    }
}
