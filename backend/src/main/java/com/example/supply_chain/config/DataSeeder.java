package com.example.supply_chain.config;

import com.example.supply_chain.entity.*;
import com.example.supply_chain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ConsumerRepository consumerRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final PricingRepository pricingRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AvailabilityRepository availabilityRepository;
    private final QualityRatingRepository qualityRatingRepository;
    private final ContractRepository contractRepository;
    private final ReviewRepository reviewRepository;

    @Value("${app.seed:false}")
    private boolean seedEnabled;

    @Override
    public void run(String... args) {
        if (!seedEnabled) return;

        if (consumerRepository.count() > 0) return;

        Consumer acme = consumerRepository.save(Consumer.builder()
                .name("Acme Corp")
                .email("purchasing@acme.test")
                .org("Acme")
                .build());

        Supplier sup1 = supplierRepository.save(Supplier.builder()
                .name("Global Supplies")
                .address("123 Supply St")
                .contact("+1-555-0100")
                .build());

        Supplier sup2 = supplierRepository.save(Supplier.builder()
                .name("Prime Materials")
                .address("456 Prime Ave")
                .contact("+1-555-0200")
                .build());

        RawMaterial steel = rawMaterialRepository.save(RawMaterial.builder()
                .name("Steel Sheet")
                .description("Cold-rolled steel")
                .category("Metals")
                .build());

        RawMaterial copper = rawMaterialRepository.save(RawMaterial.builder()
                .name("Copper Wire")
                .description("High conductivity")
                .category("Metals")
                .build());

        Pricing p1 = pricingRepository.save(Pricing.builder()
                .supplier(sup1)
                .material(steel)
                .price(new BigDecimal("120.50"))
                .validFrom(LocalDate.now().minusDays(30))
                .validTo(LocalDate.now().plusDays(60))
                .build());

        Pricing p2 = pricingRepository.save(Pricing.builder()
                .supplier(sup2)
                .material(copper)
                .price(new BigDecimal("80.00"))
                .validFrom(LocalDate.now().minusDays(10))
                .validTo(LocalDate.now().plusDays(90))
                .build());

        Availability a1 = availabilityRepository.save(Availability.builder()
                .material(steel)
                .supplier(sup1)
                .quantity(1000)
                .unit("kg")
                .build());

        Availability a2 = availabilityRepository.save(Availability.builder()
                .material(copper)
                .supplier(sup2)
                .quantity(500)
                .unit("kg")
                .build());

        QualityRating q1 = qualityRatingRepository.save(QualityRating.builder()
                .material(steel)
                .supplier(sup1)
                .aggregateScore(85)
                .build());

        QualityRating q2 = qualityRatingRepository.save(QualityRating.builder()
                .material(copper)
                .supplier(sup2)
                .aggregateScore(90)
                .build());

        Contract c1 = contractRepository.save(Contract.builder()
                .supplier(sup1)
                .consumer(acme)
                .pricing(p1)
                .startDate(LocalDate.now().minusDays(7))
                .endDate(LocalDate.now().plusMonths(6))
                .terms("Net 30, delivery FOB")
                .build());

        Order order = orderRepository.save(Order.builder()
                .consumer(acme)
                .orderDate(LocalDate.now())
                .totalAmount(new BigDecimal("241.00"))
                .build());

        orderItemRepository.save(OrderItem.builder()
                .order(order)
                .material(steel)
                .quantity(2)
                .pricePerUnit(new BigDecimal("120.50"))
                .build());

        reviewRepository.save(Review.builder()
                .supplier(sup1)
                .material(steel)
                .consumer(acme)
                .order(order)
                .rating(4)
                .comments("Timely delivery and good quality.")
                .build());
    }
}


