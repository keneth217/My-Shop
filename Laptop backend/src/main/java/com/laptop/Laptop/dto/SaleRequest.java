package com.laptop.Laptop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {
    private List<Long> productIds;
    private List<Integer> quantities;

    // Getters and Setters
}