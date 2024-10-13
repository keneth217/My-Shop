package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.PaymentResponseDto;
import com.laptop.Laptop.dto.SalaryDto;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.enums.ExpenseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
public interface BusinessService {

    StockPurchase addStockPurchase(Long productId, Long supplierId, StockPurchase stockPurchase);
    double calculateGrossProfit();
    double calculateNetProfit();
    double getTotalSales();
    double getTotalExpenses();

    Page<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Product> getTopSellingProducts(Pageable pageable);

    long totalUsersByShop();
    int totalEmployees();
    int totalProducts();
    double getTotalSalesForShop(Long shopId, String shopCode);
    double calculateNetProfitForShop(Long shopId, String shopCode);
    double calculateGrossProfitForShop(Long shopId, String shopCode);
    Page<Product> getStockAlerts(Pageable pageable);
    double getTotalInvestments();



    List<Sale> getSalesForShop(Long shopId, LocalDate startDate, LocalDate endDate);
    List<StockPurchase> getStockPurchasesForShop(Long shopId, LocalDate startDate, LocalDate endDate);


}
