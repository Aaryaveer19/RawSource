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

import com.example.supply_chain.entity.QualityRating;
import com.example.supply_chain.repository.QualityRatingRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quality-ratings")
@RequiredArgsConstructor
public class QualityRatingController {
    private final QualityRatingRepository repository;

    @GetMapping
    public List<QualityRating> all(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QualityRating> one(@PathVariable Long id){
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<QualityRating> create(@RequestBody QualityRating q){
        QualityRating saved = repository.save(q);
        return ResponseEntity.created(URI.create("/api/quality-ratings/" + saved.getQualityId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QualityRating> update(@PathVariable Long id, @RequestBody QualityRating q){
        q.setQualityId(id);
        return ResponseEntity.ok(repository.save(q));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<QualityRating> patch(@PathVariable Long id, @RequestBody QualityRating patch){
        return repository.findById(id).map(existing -> {
            BeanUtils.copyProperties(patch, existing, getNullPropertyNames(patch));
            existing.setQualityId(id);
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


