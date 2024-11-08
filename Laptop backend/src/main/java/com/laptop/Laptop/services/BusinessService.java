package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.PaymentResponseDto;
import com.laptop.Laptop.dto.SalaryDto;
import com.laptop.Laptop.dto.StockPurchaseDto;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.enums.ExpenseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface BusinessService {

    StockPurchaseDto addStockPurchase(Long productId, Long supplierId, StockPurchaseDto stockPurchaseDto);
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

    Page<Product> getOutOFStockAlerts(Pageable pageable);
    Page<Product> getTopSellingProducts(Pageable pageable);

    double getTotalSalesForShop(Long shopId, String shopCode);

    double calculateGrossProfitForShop(Long shopId, String shopCode);

    double calculateNetProfitForShop(Long shopId, String shopCode);


   List<Sale> getSalesForShop(LocalDate startDate, LocalDate endDate);

    List<StockPurchaseDto> getStockPurchasesForShop(LocalDate startDate, LocalDate endDate);

    Page<Sale> getSalesByYearMonthAndDateRange(int year, int month, LocalDate rangeStart, LocalDate rangeEnd, Pageable pageable);
    Page<Sale> getSalesByYearAndMonth(int year, int month, Pageable pageable);
    List<StockPurchaseDto> getStockPurchasesForShopBySupplierName(LocalDate startDate, LocalDate endDate, Long supplierId);
}
