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

    long totalUsersByShop();

    int totalEmployees();

    int totalProducts();

    double calculateGrossProfit();

    double calculateNetProfit();

    public double getTotalSales();

    double getTotalExpenses();

    double getTotalInvestments();

    Page<Product> getStockAlerts(Pageable pageable);

    Page<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Expense> getExpensesByType(ExpenseType expenseType, Pageable pageable);

//    List<Sale> getSalesForShop(Long shopId, LocalDate startDate, LocalDate endDate);
//
//    List<StockPurchase> getStockPurchasesForShop(Long shopId, LocalDate startDate, LocalDate endDate);


    Page<Product> getTopSellingProducts(Pageable pageable);

    double getTotalSalesForShop(Long shopId, String shopCode);

    double calculateGrossProfitForShop(Long shopId, String shopCode);

    double calculateNetProfitForShop(Long shopId, String shopCode);


   List<Sale> getSalesForShop(LocalDate startDate, LocalDate endDate);

    List<StockPurchase> getStockPurchasesForShop(LocalDate startDate, LocalDate endDate) ;

}
