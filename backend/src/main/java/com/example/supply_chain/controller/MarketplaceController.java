package com.example.supply_chain.controller;

import com.example.supply_chain.entity.*;
import com.example.supply_chain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final PricingRepository pricingRepository;
    private final AvailabilityRepository availabilityRepository;
    private final ContractRepository contractRepository;
    private final OrderRepository orderRepository;
    private final ConsumerRepository consumerRepository;

    @GetMapping("/listings")
    public ResponseEntity<List<MarketplaceListingDTO>> getListings() {
        List<Pricing> pricings = pricingRepository.findAll();
        List<MarketplaceListingDTO> listings = new ArrayList<>();

        for (Pricing p : pricings) {
            Optional<Availability> availOpt = availabilityRepository.findBySupplierAndMaterial(p.getSupplier(), p.getMaterial());
            
            MarketplaceListingDTO dto = new MarketplaceListingDTO();
            dto.setPricingId(p.getPricingId());
            dto.setSupplierId(p.getSupplier().getSupplierId());
            dto.setSupplierName(p.getSupplier().getName());
            dto.setMaterialId(p.getMaterial().getMaterialId());
            dto.setMaterialName(p.getMaterial().getName());
            dto.setMaterialDescription(p.getMaterial().getDescription());
            dto.setPrice(p.getPrice());

            if (availOpt.isPresent()) {
                dto.setQuantityAvailable(availOpt.get().getQuantity());
                dto.setUnit(availOpt.get().getUnit());
            } else {
                dto.setQuantityAvailable(0);
                dto.setUnit("units");
            }
            
            listings.add(dto);
        }

        return ResponseEntity.ok(listings);
    }

    @PostMapping("/purchase")
    @Transactional
    public ResponseEntity<?> purchase(@RequestBody PurchaseRequest request) {
        // 1. Fetch Pricing (gives us Supplier and Material)
        Pricing pricing = pricingRepository.findById(request.getPricingId())
                .orElseThrow(() -> new RuntimeException("Pricing not found"));
        
        // 2. Fetch Consumer
        Consumer consumer = consumerRepository.findById(request.getConsumerId())
                .orElseThrow(() -> new RuntimeException("Consumer not found"));

        // 3. Create Contract
        Contract contract = new Contract();
        contract.setSupplier(pricing.getSupplier());
        contract.setConsumer(consumer);
        contract.setPricing(pricing);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusMonths(1)); // Arbitrary spot contract duration
        contract.setTerms("Standard spot purchase agreement for " + request.getQuantity() + " units.");
        contract = contractRepository.save(contract);

        // 4. Create Order
        Order order = new Order();
        order.setConsumer(consumer);
        order.setContract(contract);
        order.setOrderDate(LocalDate.now());
        BigDecimal total = pricing.getPrice().multiply(new BigDecimal(request.getQuantity()));
        order.setTotalAmount(total);

        // 5. Create OrderItem
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setMaterial(pricing.getMaterial());
        item.setQuantity(request.getQuantity());
        item.setPricePerUnit(pricing.getPrice());
        
        order.getItems().add(item);
        order = orderRepository.save(order);

        // 6. Deduct from Availability
        Optional<Availability> availOpt = availabilityRepository.findBySupplierAndMaterial(pricing.getSupplier(), pricing.getMaterial());
        if (availOpt.isPresent()) {
            Availability avail = availOpt.get();
            avail.setQuantity(Math.max(0, avail.getQuantity() - request.getQuantity()));
            availabilityRepository.save(avail);
        }

        return ResponseEntity.ok(order);
    }

    @Data
    public static class MarketplaceListingDTO {
        private Long pricingId;
        private Long supplierId;
        private String supplierName;
        private Long materialId;
        private String materialName;
        private String materialDescription;
        private BigDecimal price;
        private Integer quantityAvailable;
        private String unit;
    }

    @Data
    public static class PurchaseRequest {
        private Long pricingId;
        private Long consumerId;
        private Integer quantity;
    }
}
