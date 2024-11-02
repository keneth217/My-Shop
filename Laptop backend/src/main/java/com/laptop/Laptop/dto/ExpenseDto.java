package com.laptop.Laptop.dto;

import com.laptop.Laptop.enums.ExpenseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
    private ExpenseType type;  // e.g., "Salary", "Stock Purchase", "Utilities"
    private double amount;
    private String description;
    private LocalDate date;
    private String shopCode;
}
