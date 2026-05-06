package com.example.supply_chain.controller;

import java.net.URI;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.beans.PropertyDescriptor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.example.supply_chain.entity.Review;
import com.example.supply_chain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

import com.example.supply_chain.entity.QualityRating;
import com.example.supply_chain.repository.QualityRatingRepository;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewRepository repository;
    private final QualityRatingRepository qualityRatingRepository;

    @GetMapping
    public List<Review> all(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> one(@PathVariable Long id){
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Review> create(@RequestBody Review r){
        // Save the review
        Review saved = repository.save(r);

        // Update QualityRating
        if (r.getMaterial() != null && r.getMaterial().getMaterialId() != null) {
            Long materialId = r.getMaterial().getMaterialId();
            List<Review> reviews = repository.findByMaterialMaterialId(materialId);
            
            if (!reviews.isEmpty()) {
                double avg = reviews.stream()
                        .filter(rev -> rev.getRating() != null)
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);
                
                int roundedAvg = (int) Math.round(avg);
                
                Optional<QualityRating> qrOpt = qualityRatingRepository.findByMaterialMaterialId(materialId);
                QualityRating qr;
                if (qrOpt.isPresent()) {
                    qr = qrOpt.get();
                } else {
                    qr = new QualityRating();
                    qr.setMaterial(r.getMaterial());
                    qr.setSupplier(r.getSupplier());
                }
                qr.setAggregateScore(roundedAvg);
                qualityRatingRepository.save(qr);
            }
        }

        return ResponseEntity.created(URI.create("/api/reviews/" + saved.getReviewId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> update(@PathVariable Long id, @RequestBody Review r){
        r.setReviewId(id);
        return ResponseEntity.ok(repository.save(r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Review> patch(@PathVariable Long id, @RequestBody Review patch){
        return repository.findById(id).map(existing -> {
            BeanUtils.copyProperties(patch, existing, getNullPropertyNames(patch));
            existing.setReviewId(id);
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            if (src.getPropertyValue(pd.getName()) == null) {
                emptyNames.add(pd.getName());
            }
        }
        return emptyNames.toArray(new String[0]);
    }
}


