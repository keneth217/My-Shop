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
    Sale createSale(Long productId, Sale sale) throws IOException;
    Expense addExpense(Expense expense);
    Employee addEmployee(Employee employee);
    PaymentResponseDto payEmployee(Long employeeId, SalaryDto salary);
    StockPurchase addStockPurchase(Long productId, Long supplierId, StockPurchase stockPurchase);
    double calculateGrossProfit();
    double calculateNetProfit();
    double getTotalSales();
    double getTotalExpenses();

    Page<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Product> getTopSellingProducts(Pageable pageable);
    List<Expense> getExpensesByExpenseType(ExpenseType expenseType);
    long totalUsersByShop();
    int totalEmployees();
    int totalProducts();
    double getTotalSalesForShop(Long shopId, String shopCode);
    double calculateNetProfitForShop(Long shopId, String shopCode);
    double calculateGrossProfitForShop(Long shopId, String shopCode);
    Page<Product> getStockAlerts(Pageable pageable);
    double getTotalInvestments();
    Sale createSale(List<Long> productIds, List<Integer> quantities, Sale sale);

    Investment addInvestment(Investment investment);
    Supplier addSupplier(Supplier  supplier);

    List<Sale> getSalesForShop(Long shopId, LocalDate startDate, LocalDate endDate);
    List<StockPurchase> getStockPurchasesForShop(Long shopId, LocalDate startDate, LocalDate endDate);

    Cart addToCart(Long productId, int quantity, User user);
    Sale checkoutCart(User user, String customerName, String customerPhone) throws IOException;
    Cart getCartItems(User user);
}
