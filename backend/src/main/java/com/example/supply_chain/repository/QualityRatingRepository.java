package com.example.supply_chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.supply_chain.entity.QualityRating;
import java.util.Optional;

public interface QualityRatingRepository extends JpaRepository<QualityRating, Long> {
    Optional<QualityRating> findByMaterialMaterialId(Long materialId);
}















