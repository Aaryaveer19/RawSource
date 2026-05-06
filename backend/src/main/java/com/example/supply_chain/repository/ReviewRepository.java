package com.example.supply_chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.supply_chain.entity.Review;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMaterialMaterialId(Long materialId);
}















