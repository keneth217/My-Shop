package com.laptop.Laptop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreationRequestDto {

private Long id;
    private String name;
    private double price;  // Selling price
    private double cost;   // Cost price
    private int stock;

}
