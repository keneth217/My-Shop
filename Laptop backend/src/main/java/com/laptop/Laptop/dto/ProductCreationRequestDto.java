package com.laptop.Laptop.dto;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreationRequestDto {

private Long id;
    private String name;
    private double price;  // Selling price
    private double cost;
    private List<String> productFeatures;
    private List<byte[]> productImages; // Cost price
    private int stock;

}
