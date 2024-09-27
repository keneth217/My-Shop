package com.laptop.Laptop.entity;

import com.laptop.Laptop.entity.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
@Entity
public class StockPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate purchaseDate;
    private int quantity;
    private double totalCost;

    @ManyToOne
    private Product product;

    // Getters and Setters
}