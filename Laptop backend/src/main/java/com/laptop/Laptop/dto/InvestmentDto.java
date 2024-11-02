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
public class InvestmentDto {
    private double amount;
    private String description;
    private String shopCode;
    private LocalDate date;
}
