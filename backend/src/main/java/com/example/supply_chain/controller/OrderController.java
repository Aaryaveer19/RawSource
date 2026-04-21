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

import com.example.supply_chain.entity.Order;
import com.example.supply_chain.entity.OrderItem;
import com.example.supply_chain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository repository;

    @GetMapping
    public List<Order> all(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> one(@PathVariable Long id){
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // New Endpoint: Get all items for a specific order!
    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long id){
        return repository.findById(id)
                .map(order -> ResponseEntity.ok(order.getItems()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order o){
        Order saved = repository.save(o);
        return ResponseEntity.created(URI.create("/api/orders/" + saved.getOrderId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order o){
        o.setOrderId(id);
        return ResponseEntity.ok(repository.save(o));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Order> patch(@PathVariable Long id, @RequestBody Order patch){
        return repository.findById(id).map(existing -> {
            BeanUtils.copyProperties(patch, existing, getNullPropertyNames(patch));
            existing.setOrderId(id);
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


