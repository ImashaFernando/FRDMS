
package com.frdms.frdms.distribution_management.repository;

import com.frdms.frdms.distribution_management.entity.ReliefPackage;
import com.frdms.frdms.distribution_management.entity.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReliefPackageRepository
        extends JpaRepository<ReliefPackage, Long> {

    boolean existsByRequirementId(Long requirementId);

    Optional<ReliefPackage> findByPackageCode(String packageCode);

    List<ReliefPackage> findByStatusNot(PackageStatus status);

    List<ReliefPackage> findByStatus(PackageStatus status);

    boolean existsByPackageCode(String packageCode);

    // Find packages linked to the logged-in victim's requirements
    List<ReliefPackage> findByRequirementIdIn(List<Long> requirementIds);
}
