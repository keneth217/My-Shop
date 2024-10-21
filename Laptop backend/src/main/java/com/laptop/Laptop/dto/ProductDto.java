package com.laptop.Laptop.dto;

import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.StockPurchase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String name;
    private String shopCode;
    private double price;  // Selling price
    private double cost;   // costs of each product Cost price
    private int stock;
    private int quantitySold;
    private List<String> productFeatures;  // List of features
    private List<byte[]> productImages;  // Storing images as byte arrays
    private Shop shop;  // Each product belongs to one shop
    private List<StockPurchase> stockPurchases;
}
