package com.example.supply_chain.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "\"order\"")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consumer_id", nullable = false)
    private Consumer consumer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OrderItem> items = new ArrayList<>();

    @Builder.Default
    @Column(name = "status", nullable = false, columnDefinition = "varchar(255) default 'PENDING'")
    private String status = "PENDING";
}


