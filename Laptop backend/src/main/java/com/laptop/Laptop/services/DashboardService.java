package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private BusinessService businessService;

    public double getTotalSales() {
        return businessService.getTotalSales();
    }

    public double getTotalExpenses() {
        return businessService.getTotalExpenses();
    }

    public double getGrossProfit() {
        return businessService.calculateGrossProfit();
    }

    public double getNetProfit() {
        return businessService.calculateNetProfit();
    }

    public List<Product> getStockAlerts() {
        return businessService.getStockAlerts();
    }

    public List<Product> getTopProducts() {
        return businessService.getTopSellingProducts();
    }
}
