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

    // Returns a flat DTO - avoids Jackson circular reference nulling out consumer/contract
    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Long id){
        return repository.findById(id).map(order -> {
            java.util.Map<String, Object> dto = new java.util.HashMap<>();
            dto.put("orderId", order.getOrderId());
            dto.put("status", order.getStatus());
            dto.put("orderDate", order.getOrderDate());
            dto.put("totalAmount", order.getTotalAmount());
            if (order.getContract() != null && order.getContract().getSupplier() != null) {
                dto.put("supplierId", order.getContract().getSupplier().getSupplierId());
                dto.put("supplierName", order.getContract().getSupplier().getName());
            }
            if (order.getConsumer() != null) {
                dto.put("consumerId", order.getConsumer().getConsumerId());
            }
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Get all items for a specific order - returns flat DTOs safe for JSON serialization
    @GetMapping("/{id}/items")
    public ResponseEntity<List<java.util.Map<String, Object>>> getOrderItems(@PathVariable Long id){
        return repository.findById(id).map(order -> {
            List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
            Long supplierId = null;
            if (order.getContract() != null && order.getContract().getSupplier() != null) {
                supplierId = order.getContract().getSupplier().getSupplierId();
            }
            for (OrderItem oi : order.getItems()) {
                java.util.Map<String, Object> dto = new java.util.HashMap<>();
                dto.put("itemId", oi.getItemId());
                dto.put("quantity", oi.getQuantity());
                dto.put("pricePerUnit", oi.getPricePerUnit());
                dto.put("supplierId", supplierId);
                if (oi.getMaterial() != null) {
                    dto.put("materialId", oi.getMaterial().getMaterialId());
                    dto.put("materialName", oi.getMaterial().getName());
                    dto.put("materialDescription", oi.getMaterial().getDescription());
                } else {
                    dto.put("materialId", null);
                    dto.put("materialName", "Raw Material");
                    dto.put("materialDescription", "");
                }
                items.add(dto);
            }
            return ResponseEntity.ok(items);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Order>> getOrdersForSupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(repository.findByContractSupplierSupplierId(supplierId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String newStatus = body.get("status");
        return repository.findById(id).map(existing -> {
            existing.setStatus(newStatus);
            repository.save(existing);
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("orderId", existing.getOrderId());
            result.put("status", existing.getStatus());
            return ResponseEntity.ok(result);
        }).orElse(ResponseEntity.notFound().build());
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
