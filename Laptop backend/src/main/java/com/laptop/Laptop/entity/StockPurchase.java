package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StockPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate purchaseDate;
    private int quantity;
    private double buyingPrice;
    private double sellingPrice;
    private double totalCost;
    private String supplierName;  // Corrected casing here to match convention
    private String shopCode;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonBackReference  // Handles the back-reference from StockPurchase to Supplier
    private Supplier supplier;
}
