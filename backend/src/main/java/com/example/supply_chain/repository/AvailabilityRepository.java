package com.example.supply_chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.supply_chain.entity.Availability;

import com.example.supply_chain.entity.Supplier;
import com.example.supply_chain.entity.RawMaterial;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    Optional<Availability> findBySupplierAndMaterial(Supplier supplier, RawMaterial material);
}















