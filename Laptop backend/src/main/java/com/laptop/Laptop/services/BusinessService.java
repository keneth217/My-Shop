package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.exceptions.InsufficientStockException;
import com.laptop.Laptop.exceptions.ProductNotFoundException;
import com.laptop.Laptop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private UserRepository userRepository;

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
    public Sale createSale(Long productId, Sale sale) {
        User loggedInUser = getLoggedInUser();
        String salePerson=getLoggedInUser().getUsername();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStock() < sale.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock available.");
        }
        // Deduct the sold quantity from the product's stock
        product.setStock(product.getStock() - sale.getQuantity());
        //populate total amount sold to the products when ach item is sold
        product.setPrice(product.getPrice() + (sale.getSalePrice() * sale.getQuantity()));
        product.setQuantitySold(product.getQuantitySold()+(sale.getQuantity()));
        productRepository.save(product);

        // Set the shop code and other relevant details for the sale
        sale.setShopCode(loggedInUser.getShop().getShopCode());
        sale.setProduct(product);
        sale.setSalePerson(salePerson);
        sale.setUser(loggedInUser); // Assuming the username is stored here
        sale.setShop(loggedInUser.getShop());

        // Save and return the sale
        return saleRepository.save(sale);
    }

    public Expense addExpense(Expense expense) {
        User loggedInUser = getLoggedInUser();
        expense.setShopCode(loggedInUser.getShopCode());
        expense.setUser(loggedInUser);
        expense.setDescription(expense.getDescription());
        expense.setExpenseType(ExpenseType.UTILITIES.name());
        expense.setAmount(expense.getAmount());
        expense.setShop(loggedInUser.getShop());
        return expenseRepository.save(expense);
    }

    @Transactional
    public Employee addEmployee(Employee employee) {
        User loggedInUser = getLoggedInUser();
        employee.setShop(loggedInUser.getShop());
        employeeRepository.save(employee);

        // Automatically track employee salary as an expense
        Expense salaryExpense = Expense.builder()
                .expenseType(ExpenseType.SALARY.name())
                .amount(employee.getSalary())
                .date(LocalDate.now())
                .shopCode(loggedInUser.getShopCode())
                .user(loggedInUser)
                .shop(loggedInUser.getShop())
                .build();
        expenseRepository.save(salaryExpense);

        return employee;
    }

    @Transactional
    public StockPurchase addStockPurchase(Long productId, StockPurchase stockPurchase) {
        User loggedInUser = getLoggedInUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setStock(product.getStock() + stockPurchase.getQuantity());
        productRepository.save(product);

        stockPurchase.setProduct(product);
        stockPurchase.setUser(loggedInUser);
        stockPurchase.setShop(loggedInUser.getShop());
        stockPurchaseRepository.save(stockPurchase);

        Expense stockExpense = Expense.builder()
                .expenseType(ExpenseType.STOCKPURCHASE.name())
                .amount(stockPurchase.getTotalCost())
                .shopCode(loggedInUser.getShopCode())
                .date(LocalDate.now())
                .user(loggedInUser)
                .shop(loggedInUser.getShop())
                .build();
        expenseRepository.save(stockExpense);

        return stockPurchase;
    }

    // Calculation methods for the business operations
    public double calculateGrossProfit() {
        User loggedInUser = getLoggedInUser();
        double totalRevenue = saleRepository.findByShop(loggedInUser.getShop()).stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();

        double totalStockCosts = saleRepository.findByShop(loggedInUser.getShop()).stream()
                .mapToDouble(sale -> sale.getProduct().getCost() * sale.getQuantity()).sum();

        return totalRevenue - totalStockCosts;
    }

    public double calculateNetProfit() {
        return calculateGrossProfit() - getTotalExpenses();
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

    // Stock alerts for low stock products
    public List<Product> getStockAlerts() {
        User loggedInUser = getLoggedInUser();
        return productRepository.findByShop(loggedInUser.getShop()).stream()
                .filter(product -> product.getStock() < 5)
                .collect(Collectors.toList());
    }

    // Retrieve sales for a specific date range
    public List<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        User loggedInUser = getLoggedInUser();
        return saleRepository.findByShopAndDateBetween(loggedInUser.getShop(), startDate, endDate);
    }

    // Top-selling products logic
    public List<Product> getTopSellingProducts() {
        User loggedInUser = getLoggedInUser();
        return saleRepository.findByShop(loggedInUser.getShop()).stream()
                .collect(Collectors.groupingBy(Sale::getProduct, Collectors.summingInt(Sale::getQuantity)))
                .entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Filter expenses by type
    public List<Expense> getExpensesByExpenseType(ExpenseType expenseType) {
        User loggedInUser = getLoggedInUser();
        return expenseRepository.findByShopAndExpenseType(loggedInUser.getShop(), expenseType.name());
    }

    // Methods for calculating total sales, expenses, and profits for a specific shop
    public double getTotalSalesForShop(Long shopId, String shopCode) {
        return saleRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();
    }

    public double getTotalExpensesForShop(Long shopId, String shopCode) {
        return expenseRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(Expense::getAmount).sum();
    }

    public double calculateGrossProfitForShop(Long shopId, String shopCode) {
        double totalRevenue = saleRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();

        double totalStockCosts = saleRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(sale -> sale.getProduct().getCost() * sale.getQuantity()).sum();

        return totalRevenue - totalStockCosts;
    }

    public double calculateNetProfitForShop(Long shopId, String shopCode) {
        return calculateGrossProfitForShop(shopId, shopCode) - getTotalExpensesForShop(shopId, shopCode);
    }

    // Count methods for the total products, employees, and users
    public int totalProducts() {
        User loggedInUser = getLoggedInUser();
        return (int) productRepository.countByShop(loggedInUser.getShop());
    }

    public int totalEmployees() {
        User loggedInUser = getLoggedInUser();
        return (int) employeeRepository.countByShop(loggedInUser.getShop());
    }

    public long totalUsersByShop() {
        User loggedInUser = getLoggedInUser(); // Retrieve logged-in user
        return userRepository.countByShop(loggedInUser.getShop()); // Return count as long
    }

    // Utility method to get the logged-in user's shop
    private Shop getLoggedInUserShop() {
        User loggedInUser = getLoggedInUser(); // Method to retrieve logged-in user
        return loggedInUser.getShop();
    }
    // Count users for the logged-in user's shop
    public long totalUsersForShop() {
        Shop shop = getLoggedInUserShop();
        return userRepository.countByShop(shop);
    }
}