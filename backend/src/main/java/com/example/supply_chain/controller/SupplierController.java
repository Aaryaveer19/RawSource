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

import com.example.supply_chain.entity.Supplier;
import com.example.supply_chain.entity.RawMaterial;
import com.example.supply_chain.entity.Contract;
import com.example.supply_chain.entity.Review;
import com.example.supply_chain.entity.Pricing;
import com.example.supply_chain.entity.Availability;
import com.example.supply_chain.entity.QualityRating;
import com.example.supply_chain.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierRepository repository;

    @GetMapping
    public List<Supplier> all(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> one(@PathVariable Long id){
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // New Hierarchical Endpoints based on the ER Diagram

    @GetMapping("/{id}/contracts")
    public ResponseEntity<List<Contract>> getSupplierContracts(@PathVariable Long id){
        return repository.findById(id).map(supplier -> ResponseEntity.ok(supplier.getContracts())).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<Review>> getSupplierReviews(@PathVariable Long id){
        return repository.findById(id).map(supplier -> ResponseEntity.ok(supplier.getReviews())).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/pricing")
    public ResponseEntity<List<Pricing>> getSupplierPricings(@PathVariable Long id){
        return repository.findById(id).map(supplier -> ResponseEntity.ok(supplier.getPricings())).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<Availability>> getSupplierAvailabilities(@PathVariable Long id){
        return repository.findById(id).map(supplier -> ResponseEntity.ok(supplier.getAvailabilities())).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<QualityRating>> getSupplierRatings(@PathVariable Long id){
        return repository.findById(id).map(supplier -> ResponseEntity.ok(supplier.getQualityRatings())).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Supplier> create(@RequestBody Supplier c){
        Supplier saved = repository.save(c);
        return ResponseEntity.created(URI.create("/api/suppliers/" + saved.getSupplierId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> update(@PathVariable Long id, @RequestBody Supplier c){
        c.setSupplierId(id);
        return ResponseEntity.ok(repository.save(c));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Supplier> patch(@PathVariable Long id, @RequestBody Supplier patch){
        return repository.findById(id).map(existing -> {
            BeanUtils.copyProperties(patch, existing, getNullPropertyNames(patch));
            existing.setSupplierId(id);
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


