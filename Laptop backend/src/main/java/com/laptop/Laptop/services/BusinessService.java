package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private StockPurchaseRepository stockPurchaseRepository;




    // Create sale and link it with product and employee
    public Sale createSale(Long productId, Sale sale) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStock(product.getStock() - sale.getQuantity());
        productRepository.save(product);

        sale.setProduct(product);  // Link the product to the sale
        return saleRepository.save(sale);
    }

    // Add a categorized expense
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    // Add employee and track salary as an expense
    public Employee addEmployee(Employee employee) {
        employeeRepository.save(employee);

        Expense salaryExpense = Expense.builder()
                .category(String.valueOf(ExpenseType.SALARY))
                .amount(employee.getSalary())
                .date(LocalDate.now())
                .build();
        expenseRepository.save(salaryExpense);
        return employee;
    }

    // Add stock purchase and update product stock
    public StockPurchase addStockPurchase(Long productId, StockPurchase stockPurchase) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStock(product.getStock() + stockPurchase.getQuantity());
        productRepository.save(product);

        stockPurchase.setProduct(product);  // Link the product to stock purchase
        return stockPurchaseRepository.save(stockPurchase);
    }

    // Profit and loss reporting
    public double calculateGrossProfit() {
        double totalRevenue = saleRepository.findAll().stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();

        double totalStockCosts = saleRepository.findAll().stream()
                .mapToDouble(sale -> sale.getProduct().getCost() * sale.getQuantity()).sum();

        return totalRevenue - totalStockCosts;
    }

    public double calculateNetProfit() {
        double grossProfit = calculateGrossProfit();
        double totalExpenses = expenseRepository.findAll().stream()
                .mapToDouble(Expense::getAmount).sum();

        return grossProfit - totalExpenses;
    }

    // KPI methods
    public double getTotalSales() {
        return saleRepository.findAll().stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();
    }

    public double getTotalExpenses() {
        return expenseRepository.findAll().stream()
                .mapToDouble(Expense::getAmount).sum();
    }

    // Stock alerts
    public List<Product> getStockAlerts() {
        return productRepository.findAll().stream()
                .filter(product -> product.getStock() < 5) // Example: alert if stock < 5
                .collect(Collectors.toList());
    }

    // Sales filtering by date range
    public List<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByDateBetween(startDate, endDate);
    }

    // Top selling products
    public List<Product> getTopSellingProducts() {
        return saleRepository.findAll().stream()
                .collect(Collectors.groupingBy(Sale::getProduct, Collectors.summingInt(Sale::getQuantity)))
                .entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .map(e -> e.getKey())
                .collect(Collectors.toList()).reversed();
    }

    // Expenses by category
    public List<Expense> getExpensesByCategory(ExpenseType category) {
        return expenseRepository.findByCategory(String.valueOf(category));
    }

    // Stock flow analysis
    public List<Product> getStockFlow() {
        return productRepository.findAll();
    }

    // Sales for specific period
    public List<Sale> getSalesForPeriod(LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByDateBetween(startDate, endDate);
    }
}
