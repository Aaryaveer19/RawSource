package com.example.supply_chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.supply_chain.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}



