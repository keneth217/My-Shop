package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor  // Add this
@AllArgsConstructor //
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

    @OneToMany(mappedBy = "supplier")
    private List<StockPurchase> stockPurchases;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

}
