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

import com.example.supply_chain.entity.RawMaterial;
import com.example.supply_chain.entity.Availability;
import com.example.supply_chain.entity.Pricing;
import com.example.supply_chain.entity.QualityRating;
import com.example.supply_chain.repository.RawMaterialRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {
    private final RawMaterialRepository repository;

    @GetMapping
    public List<RawMaterial> all(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterial> one(@PathVariable Long id){
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // New Hierarchical Endpoints based on the ER Diagram
    @GetMapping("/{id}/pricing")
    public ResponseEntity<List<Pricing>> getMaterialPricings(@PathVariable Long id){
        return repository.findById(id).map(material -> ResponseEntity.ok(material.getPricings())).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<Availability>> getMaterialAvailabilities(@PathVariable Long id){
        return repository.findById(id).map(material -> ResponseEntity.ok(material.getAvailabilities())).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<QualityRating>> getMaterialRatings(@PathVariable Long id){
        return repository.findById(id).map(material -> ResponseEntity.ok(material.getQualityRatings())).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RawMaterial> create(@RequestBody RawMaterial m){
        RawMaterial saved = repository.save(m);
        return ResponseEntity.created(URI.create("/api/raw-materials/" + saved.getMaterialId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterial> update(@PathVariable Long id, @RequestBody RawMaterial m){
        m.setMaterialId(id);
        return ResponseEntity.ok(repository.save(m));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RawMaterial> patch(@PathVariable Long id, @RequestBody RawMaterial patch){
        return repository.findById(id).map(existing -> {
            BeanUtils.copyProperties(patch, existing, getNullPropertyNames(patch));
            existing.setMaterialId(id);
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


