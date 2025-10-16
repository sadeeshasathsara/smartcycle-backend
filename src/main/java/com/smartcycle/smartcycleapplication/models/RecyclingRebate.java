package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "recycling_rebates")
@Data
public class RecyclingRebate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rebateId;
    private double amount;
    private String criteria;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
}