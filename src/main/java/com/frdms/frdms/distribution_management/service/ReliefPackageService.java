package com.frdms.frdms.distribution_management.service;

import com.frdms.frdms.distribution_management.entity.ReliefPackage;
import com.frdms.frdms.distribution_management.entity.ReliefPackageItem;
import com.frdms.frdms.distribution_management.repository.ReliefPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.frdms.frdms.distribution_management.entity.PackageStatus;

import java.util.List;

@Service
public class ReliefPackageService {

    private final ReliefPackageRepository packageRepository;

    public ReliefPackageService(ReliefPackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }


    // Save a new package
    public ReliefPackage savePackage(ReliefPackage reliefPackage) {

        // Generate package ID automatically for new packages
        if (reliefPackage.getPackageCode() == null ||
                reliefPackage.getPackageCode().isBlank()) {

            reliefPackage.setPackageCode(generatePackageCode());
        }

        // Make sure every item belongs to this package
        for (ReliefPackageItem item : reliefPackage.getItems()) {
            item.setReliefPackage(reliefPackage);
        }

        return packageRepository.save(reliefPackage);
    }


    // Get all packages
    public List<ReliefPackage> getAllPackages() {
        return packageRepository.findByStatusNot(PackageStatus.CANCELLED);
    }


    // Get one package by database ID
    @Transactional(readOnly = true)
    public ReliefPackage getPackageById(Long id) {

        return packageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Package not found with ID: " + id
                        ));
    }
    public void cancelPackage(Long id) {

        ReliefPackage reliefPackage =
                packageRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Package not found with ID: " + id
                                ));

        reliefPackage.setStatus(PackageStatus.CANCELLED);

        packageRepository.save(reliefPackage);
    }
    public List<ReliefPackage> getCancelledPackages() {

        return packageRepository.findByStatus(PackageStatus.CANCELLED);
    }
    public void restorePackage(Long id) {

        ReliefPackage reliefPackage =
                packageRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Package not found with ID: " + id
                                ));

        if (reliefPackage.getStatus() == PackageStatus.CANCELLED) {
            reliefPackage.setStatus(PackageStatus.REQUESTING_ITEMS);
            packageRepository.save(reliefPackage);
        }
    }
    public void permanentlyDeletePackage(Long id) {

        ReliefPackage reliefPackage =
                packageRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Package not found with ID: " + id
                                ));

        if (reliefPackage.getStatus() == PackageStatus.CANCELLED) {
            packageRepository.delete(reliefPackage);
        }
    }
    // Generate PKG-001, PKG-002, PKG-003...
    private String generatePackageCode() {

        int nextNumber = 1;

        while (packageRepository.existsByPackageCode(
                String.format("PKG-%03d", nextNumber))) {

            nextNumber++;
        }

        return String.format("PKG-%03d", nextNumber);
    }
}
