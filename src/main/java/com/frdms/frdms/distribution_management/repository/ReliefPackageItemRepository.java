package com.frdms.frdms.distribution_management.repository;

import com.frdms.frdms.distribution_management.entity.ReliefPackageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReliefPackageItemRepository
        extends JpaRepository<ReliefPackageItem, Long> {

    @Query("""
        SELECT item
        FROM ReliefPackageItem item
        WHERE item.reliefPackage.status <> 'CANCELLED'
        """)
    List<ReliefPackageItem> findAllActivePackageItems();
}
