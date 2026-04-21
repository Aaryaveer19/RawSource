package com.example.supply_chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.supply_chain.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {}



