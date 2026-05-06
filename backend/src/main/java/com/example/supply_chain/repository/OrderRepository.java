package com.example.supply_chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.supply_chain.entity.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByContractSupplierSupplierId(Long supplierId);
}


