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

import com.example.supply_chain.entity.Consumer;
import com.example.supply_chain.entity.Order;
import com.example.supply_chain.repository.ConsumerRepository;

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
@RequestMapping("/api/consumers")
@RequiredArgsConstructor
public class ConsumerController {
    private final ConsumerRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    @GetMapping
    public List<Consumer> all(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consumer> one(@PathVariable Long id){
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // New Endpoint: Get all orders for a specific consumer!
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<Order>> getConsumerOrders(@PathVariable Long id){
        return repository.findById(id)
                .map(consumer -> ResponseEntity.ok(consumer.getOrders()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Consumer> create(@RequestBody Consumer c){
        Consumer saved = repository.save(c);
        return ResponseEntity.created(URI.create("/api/consumers/" + saved.getConsumerId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consumer> update(@PathVariable Long id, @RequestBody Consumer c){
        c.setConsumerId(id);
        return ResponseEntity.ok(repository.save(c));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Consumer> patch(@PathVariable Long id, @RequestBody Consumer patch){
        return repository.findById(id).map(existing -> {
            BeanUtils.copyProperties(patch, existing, getNullPropertyNames(patch));
            existing.setConsumerId(id);
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

        Consumer consumer = new Consumer();
        consumer.setName(request.getName());
        consumer.setEmail(request.getEmail());
        consumer.setPhone(request.getPhone());
        consumer.setPassword(passwordEncoder.encode(request.getPassword()));

        Consumer savedConsumer = repository.save(consumer);
        String token = generateToken(savedConsumer.getEmail());

        response.put("errorCode", "200");
        response.put("token", token);
        response.put("consumer", savedConsumer);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        Optional<Consumer> consumerOpt = repository.findByEmail(request.getEmail());
        if (consumerOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), consumerOpt.get().getPassword())) {
            response.put("errorCode", "401");
            response.put("errorDesc", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }

        Consumer consumer = consumerOpt.get();
        String token = generateToken(consumer.getEmail());

        response.put("errorCode", "200");
        response.put("token", token);
        response.put("consumer", consumer);
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


