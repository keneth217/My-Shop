package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.DashboardDTO;
import com.laptop.Laptop.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private BusinessService businessService;

    public DashboardDTO getDashboardData() {
        double totalSales = businessService.getTotalSales();
        double totalExpenses = businessService.getTotalExpenses();
        double grossProfit = businessService.calculateGrossProfit();
        double netProfit = businessService.calculateNetProfit();
        int totalProducts=businessService.totalProducts();
        int totalUsers= businessService.totalUsers();
        int totalEmployees= businessService.totalEmployees();
        List<Product> lowStockProducts = businessService.getStockAlerts();
        List<Product> topProducts = businessService.getTopSellingProducts();

        return DashboardDTO.builder()
                .totalSales(totalSales)
                .totalExpenses(totalExpenses)
                .grossProfit(grossProfit)
                .netProfit(netProfit)
                .stockAlerts(lowStockProducts)
                .totalProducts(totalProducts)
                .totalEmployees(totalEmployees)
                .totalUsers(totalUsers)
                .topProducts(topProducts)
                .build();
    }
}