package com.laptop.Laptop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor  // Add this
@AllArgsConstructor //
@Entity
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private double salePrice;
    private int quantity;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Employee employee;  // Track which employee made the sale

    // Getters and Setters
}
