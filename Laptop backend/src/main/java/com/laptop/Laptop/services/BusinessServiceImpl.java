package com.laptop.Laptop.services;

import com.itextpdf.io.IOException;
import com.laptop.Laptop.dto.PaymentResponseDto;
import com.laptop.Laptop.dto.SalaryDto;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.exceptions.InsufficientStockException;
import com.laptop.Laptop.exceptions.ProductNotFoundException;
import com.laptop.Laptop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StockPurchaseRepository stockPurchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private PdfReportServices pdfReportServices;
    private static final int LOW_STOCK_THRESHOLD = 5;

    // Utility method to get the logged-in user's details (shopId, shopCode, username) from token
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Assuming your `User` implements `UserDetails`
    }

    // Method to get specific details of the logged-in user
    public User getLoggedInUserDetails() {
        return getLoggedInUser(); // Directly return the logged-in user
    }


    @Transactional
    public StockPurchase addStockPurchase(Long productId, Long supplierId, StockPurchase stockPurchase) {
        User loggedInUser = getLoggedInUser();

        // Check if the product exists in the shop associated with the logged-in user
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getShop().getId().equals(loggedInUser.getShop().getId())) {
            throw new IllegalArgumentException("You cannot stock a product not associated with your shop.");
        }

        // Fetch the supplier by supplierId
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        // Update the product's stock and price
        product.setCost(stockPurchase.getItemCost());
        product.setStock(product.getStock() + stockPurchase.getQuantity());
        product.setPrice(stockPurchase.getTotalCost() + (stockPurchase.getQuantity() * stockPurchase.getItemCost()));
        productRepository.save(product);

        // Set the total cost of the stock purchase
        stockPurchase.setTotalCost(stockPurchase.getQuantity() * stockPurchase.getItemCost());

        // Set relationships and other fields
        stockPurchase.setProduct(product);
        stockPurchase.setUser(loggedInUser);
        stockPurchase.setShopCode(loggedInUser.getShopCode());

        stockPurchase.setSupplierName(supplier.getSupplierName());
        stockPurchase.setSupplier(supplier); // Set the supplier
        stockPurchase.setPurchaseDate(LocalDate.now());
        stockPurchase.setShop(loggedInUser.getShop());
        stockPurchaseRepository.save(stockPurchase);

        // Log the stock purchase as an expense
        Expense stockExpense = Expense.builder()
                .expenseType(ExpenseType.STOCKPURCHASE.name())
                .amount(stockPurchase.getTotalCost())
                .shopCode(loggedInUser.getShopCode())
                .description(product.getName() + "-" + ExpenseType.STOCKPURCHASE.name())
                .date(LocalDate.now())
                .user(loggedInUser)
                .shop(loggedInUser.getShop())
                .build();
        expenseRepository.save(stockExpense);

        return stockPurchase;
    }

    @Override
    public long totalUsersByShop() {
        User loggedInUser = getLoggedInUser();
        return userRepository.countByShop(loggedInUser.getShop());
    }

    @Override
    public int totalEmployees() {
        User loggedInUser = getLoggedInUser();
        return (int) employeeRepository.countByShop(loggedInUser.getShop());
    }

    @Override
    public int totalProducts() {
        User loggedInUser = getLoggedInUser();
        return (int) productRepository.countByShop(loggedInUser.getShop());
    }

    @Override
    public double getTotalSalesForShop(Long shopId, String shopCode) {
        // Get total sales for a specific shop based on shop ID and code
        return saleRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();
    }

    @Override
    public double calculateNetProfitForShop(Long shopId, String shopCode) {
        // Calculate the net profit for a specific shop
        double grossProfit = calculateGrossProfitForShop(shopId, shopCode);
        double totalExpenses = expenseRepository.findByShopIdAndShopCode(shopId, shopCode)
                .stream().mapToDouble(Expense::getAmount).sum();
        double totalInvestments = investmentRepository.findByShopIdAndShopCode(shopId, shopCode)
                .stream().mapToDouble(Investment::getAmount).sum();

        return grossProfit - totalExpenses + totalInvestments;
    }

    @Override
    public double calculateGrossProfitForShop(Long shopId, String shopCode) {
        // Calculate the gross profit for a specific shop
        double totalRevenue = saleRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();

        double totalStockCosts = stockPurchaseRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(s -> s.getProduct().getCost() * s.getQuantity())
                .sum();

        return totalRevenue - totalStockCosts;
    }












    public double calculateGrossProfit() {
        User loggedInUser = getLoggedInUser();
        double totalRevenue = saleRepository.findByShop(loggedInUser.getShop()).stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();

//        double totalStockCosts = stockPurchaseRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
//                .mapToDouble(s -> s.getProduct().getCost() * s.getQuantity())
//                .sum();

        double totalStockCosts = stockPurchaseRepository.findByShop(loggedInUser.getShop()).stream()
                .mapToDouble(sale -> sale.getProduct().getCost() * sale.getQuantity()).sum();

        return totalRevenue - totalStockCosts;
    }

    public double calculateNetProfit() {
        double grossProfit = calculateGrossProfit();
        double totalExpenses = getTotalExpenses();
        double totalInvestments = getTotalInvestments();
        return grossProfit - totalExpenses + totalInvestments;
    }

    public double getTotalSales() {
        User loggedInUser = getLoggedInUser();
        return saleRepository.findByShop(loggedInUser.getShop()).stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();
    }

    public double getTotalExpenses() {
        User loggedInUser = getLoggedInUser();
        return expenseRepository.findByShop(loggedInUser.getShop()).stream()
                .mapToDouble(Expense::getAmount).sum();
    }



    @Override
    public Page<Product> getStockAlerts(Pageable pageable) {
        // Fetch products where stock is below the threshold with pagination
        return productRepository.findAllByStockLessThanEqual(LOW_STOCK_THRESHOLD, pageable);
    }


    public Page<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // Fetch sales records within the specified date range with pagination
        return saleRepository.findAllByDateBetween(startDate, endDate, pageable);
    }


    public Page<Product> getTopSellingProducts(Pageable pageable) {
        // Fetch top selling products with pagination
        return productRepository.findTopSellingProducts(pageable);
    }


    public Page<Expense> getExpensesByExpenseType(ExpenseType expenseType, Pageable pageable) {
        // Fetch expenses filtered by the given expense type with pagination
        return expenseRepository.findAllByExpenseType(expenseType.name(), pageable);
    }

    public double getTotalInvestments() {
        User loggedInUser = getLoggedInUser();
        return investmentRepository.findByShop(loggedInUser.getShop()).stream()
                .mapToDouble(Investment::getAmount).sum();
    }

    // Method to fetch sales for a shop within a date range
    public List<Sale> getSalesForShop(Long shopId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByShopIdAndDateBetween(shopId, startDate, endDate);
    }

    // Method to fetch stock purchases for a shop within a date range
    public List<StockPurchase> getStockPurchasesForShop(Long shopId, LocalDate startDate, LocalDate endDate) {
        return stockPurchaseRepository.findByShopIdAndPurchaseDateBetween(shopId, startDate, endDate);
    }








}
