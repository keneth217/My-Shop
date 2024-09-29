package com.laptop.Laptop.dto;

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
    private List<Product> stockAlerts;
    private List<Product> topProducts;
    private int totalProducts;
    private int totalUsers;
    private int totalEmployees;
}
