package com.laptop.Laptop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockPurchaseDto {
    private Long id;
    private LocalDate purchaseDate;
    private int quantity;
    private double buyingPrice;
    private double sellingPrice;
    private double totalCost;
    private String supplierName;
    private String shopCode;
}
