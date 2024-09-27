package com.laptop.Laptop.controller;

import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/total-sales")
    public ResponseEntity<Double> getTotalSales() {
        double totalSales = dashboardService.getTotalSales();
        return ResponseEntity.ok(totalSales);
    }

    @GetMapping("/total-expenses")
    public ResponseEntity<Double> getTotalExpenses() {
        double totalExpenses = dashboardService.getTotalExpenses();
        return ResponseEntity.ok(totalExpenses);
    }

    @GetMapping("/gross-profit")
    public ResponseEntity<Double> getGrossProfit() {
        double grossProfit = dashboardService.getGrossProfit();
        return ResponseEntity.ok(grossProfit);
    }

    @GetMapping("/net-profit")
    public ResponseEntity<Double> getNetProfit() {
        double netProfit = dashboardService.getNetProfit();
        return ResponseEntity.ok(netProfit);
    }

    @GetMapping("/stock-alerts")
    public ResponseEntity<List<Product>> getStockAlerts() {
        List<Product> stockAlerts = dashboardService.getStockAlerts();
        return ResponseEntity.ok(stockAlerts);
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<Product>> getTopProducts() {
        List<Product> topProducts = dashboardService.getTopProducts();
        return ResponseEntity.ok(topProducts);
    }
}
