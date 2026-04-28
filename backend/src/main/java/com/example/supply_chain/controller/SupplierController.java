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
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.security.Key;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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

    // ---- Auth Endpoints ----

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            response.put("errorCode", "400");
            response.put("errorDesc", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            response.put("errorCode", "400");
            response.put("errorDesc", "Passwords do not match");
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getPassword().length() < 6) {
            response.put("errorCode", "400");
            response.put("errorDesc", "Password must be at least 6 characters");
            return ResponseEntity.badRequest().body(response);
        }

        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setContact(request.getPhone());
        supplier.setPassword(passwordEncoder.encode(request.getPassword()));

        Supplier savedSupplier = repository.save(supplier);
        String token = generateToken(savedSupplier.getEmail());

        response.put("errorCode", "200");
        response.put("token", token);
        response.put("supplier", savedSupplier);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        Optional<Supplier> supplierOpt = repository.findByEmail(request.getEmail());
        if (supplierOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), supplierOpt.get().getPassword())) {
            response.put("errorCode", "401");
            response.put("errorDesc", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }

        Supplier supplier = supplierOpt.get();
        String token = generateToken(supplier.getEmail());

        response.put("errorCode", "200");
        response.put("token", token);
        response.put("supplier", supplier);
        return ResponseEntity.ok(response);
    }

    private String generateToken(String email) {
        long expireTime = 1000L * 60 * 60 * 24; // 24 hours
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(jwtSecretKey)
                .compact();
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

    @Data
    public static class RegisterRequest {
        private String name;
        private String email;
        private String phone;
        private String password;
        private String confirmPassword;
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
}

