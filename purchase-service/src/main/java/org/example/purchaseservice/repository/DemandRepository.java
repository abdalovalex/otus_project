package org.example.purchaseservice.repository;

import org.example.purchaseservice.entity.Demand;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandRepository extends JpaRepository<Demand, Integer> {
}
