package com.laptop.Laptop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDto {
private Long id;
    private String supplierName;
    private String supplierAddress;
    private String supplierPhone;
    private String supplierLocation;
    private String shopCode;
    private List<StockPurchaseDto> stockPurchases;
}
