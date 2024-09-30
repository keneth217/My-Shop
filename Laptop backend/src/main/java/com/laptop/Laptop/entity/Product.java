package com.laptop.Laptop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String shopCode;
    private double price;  // Selling price
    private double cost;   // Cost price
    private int stock;
    private int quantitySold;
    @ElementCollection
    private List<String> productFeatures;
    @ElementCollection
    @Lob
    @Column(columnDefinition = "longblob")
    private List<byte[]> productImages; // More images field
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop; // Each product belongs to one shop

    // Getters and Setters
}