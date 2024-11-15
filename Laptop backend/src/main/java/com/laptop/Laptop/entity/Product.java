package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.laptop.Laptop.audit.AuditAware;
import com.laptop.Laptop.audit.AuditLogListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

@EntityListeners(value = {AuditLogListener.class})
public class Product implements Serializable, AuditAware {
//public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String shopCode;
    private double sellingPrice;  // Selling price
    private double cost;   // costs of each product Cost price
    private int stock;
    private int quantitySold;

    @ElementCollection
    private List<String> productFeatures;  // List of features

    @ElementCollection
    @Lob
    @Column(columnDefinition = "longblob")
    private List<byte[]> productImages;  // Storing images as byte arrays

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "shop_id",nullable = false)
    private Shop shop;  // Each product belongs to one shop


    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<StockPurchase> stockPurchases;
}