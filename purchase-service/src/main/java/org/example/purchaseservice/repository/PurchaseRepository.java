package org.example.purchaseservice.repository;

import java.util.List;
import java.util.Optional;

import org.example.purchaseservice.entity.Purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    @Query(value = """
            SELECT purchase
            FROM Purchase purchase
            WHERE purchase.state = org.example.purchaseservice.entity.StatePurchase.DEMAND_START
            AND purchase.demandEndDateTime <= CURRENT_TIMESTAMP
            """)
    List<Purchase> findByEndDemand();

    @Query(value = """
            SELECT purchase
            FROM Purchase purchase
            LEFT JOIN FETCH purchase.demands demands
            WHERE purchase.id = :id
            """)
    Optional<Purchase> findByIdWithDemands(@Param("id") Integer id);
}
