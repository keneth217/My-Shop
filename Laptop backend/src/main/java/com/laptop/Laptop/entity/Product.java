package com.laptop.Laptop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // Add this
@AllArgsConstructor //
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;  // Selling price
    private double cost;   // Cost price
    private int stock;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop; // Each product belongs to one shop

    // Getters and Setters
}