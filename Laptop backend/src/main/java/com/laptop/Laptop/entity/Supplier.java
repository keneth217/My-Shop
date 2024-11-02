package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String supplierName;
    private String supplierAddress;
    private String supplierPhone;
    private String supplierLocation;
    private String shopCode;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    @JsonManagedReference  // Manages the relationship from the Supplier side
    private List<StockPurchase> stockPurchases;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    @JsonIgnore  // Ignore this in JSON serialization to avoid unnecessary complexity
    private Shop shop;
}
