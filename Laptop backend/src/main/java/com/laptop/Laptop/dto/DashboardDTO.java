package com.laptop.Laptop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.laptop.Laptop.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardDTO {

    private double totalSales;
    private double totalExpenses;
    private double grossProfit;
    private double netProfit;
    @JsonIgnore
    private List<Product> stockAlerts;
    @JsonIgnore
    private List<Product> topProducts;
    private int totalProducts;
    private long totalUsers;
    private int totalEmployees;
}
