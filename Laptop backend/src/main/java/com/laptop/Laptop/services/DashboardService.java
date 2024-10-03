package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.DashboardDTO;
import com.laptop.Laptop.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private BusinessService businessService;

    public DashboardDTO getDashboardData(Pageable pageable) {
        double totalSales = businessService.getTotalSales();
        double totalExpenses = businessService.getTotalExpenses();
        double grossProfit = businessService.calculateGrossProfit();
        double netProfit = businessService.calculateNetProfit();
        int totalProducts = businessService.totalProducts();
        long totalUsers = businessService.totalUsersByShop();
        int totalEmployees = businessService.totalEmployees();
        Page<Product> lowStockProducts = businessService.getStockAlerts(pageable);

        // Fetch top products as a Page
        Page<Product> topProductsPage = businessService.getTopSellingProducts(pageable);

        // Convert Page<Product> to List<Product>
        List<Product> topProducts = topProductsPage.getContent();
        List<Product> lowStock = lowStockProducts.getContent();
        return DashboardDTO.builder()
                .totalSales(totalSales)
                .totalExpenses(totalExpenses)
                .grossProfit(grossProfit)
                .netProfit(netProfit)
                .stockAlerts(lowStock)
                .totalProducts(totalProducts)
                .totalEmployees(totalEmployees)
                .totalUsers(totalUsers)
                .topProducts(topProducts)
                .build();
    }
}
