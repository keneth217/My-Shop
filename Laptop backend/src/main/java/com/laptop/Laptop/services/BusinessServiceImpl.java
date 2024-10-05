package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.PaymentResponseDto;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    private InvestmentRepository investmentRepository;


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

        double totalStockCosts = saleRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(sale -> sale.getProduct().getCost() * sale.getQuantity()).sum();

        return totalRevenue - totalStockCosts;
    }

    @Transactional
    public Sale createSale(Long productId, Sale sale) {
        User loggedInUser = getLoggedInUser();
        String salePerson = getLoggedInUser().getUsername();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStock() < sale.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock available.");
        }
        // Deduct the sold quantity from the product's stock
        product.setStock(product.getStock() - sale.getQuantity());
        // Populate total amount sold to the product when each item is sold
        product.setPrice(product.getPrice() + (sale.getSalePrice() * sale.getQuantity()));
        product.setQuantitySold(product.getQuantitySold() + sale.getQuantity());
        productRepository.save(product);

        // Set the shop code and other relevant details for the sale
        sale.setShopCode(loggedInUser.getShop().getShopCode());
        sale.setProduct(product);
        sale.setDate(LocalDate.now());
        sale.setSalePerson(salePerson);
        sale.setUser(loggedInUser);
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
        expense.setDate(LocalDate.now());
        expense.setShop(loggedInUser.getShop());
        return expenseRepository.save(expense);
    }

    @Transactional
    public Employee addEmployee(Employee employee) {
        User loggedInUser = getLoggedInUser();
        employee.setShop(loggedInUser.getShop());
        return employeeRepository.save(employee);
    }

    @Transactional
    public PaymentResponseDto payEmployee(Long employeeId, double salaryAmount) {
        User loggedInUser = getLoggedInUser();

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalStateException("Employee not found"));

        if (!employee.getShop().getId().equals(loggedInUser.getShop().getId())) {
            throw new IllegalStateException("Unauthorized to pay this employee");
        }

        Payment salaryPayment = Payment.builder()
                .employee(employee)
                .amount(salaryAmount)
                .paymentDate(LocalDate.now())
                .build();
        paymentRepository.save(salaryPayment);

        Expense salaryExpense = Expense.builder()
                .expenseType(ExpenseType.SALARY.name())
                .amount(salaryAmount)
                .date(LocalDate.now())
                .shopCode(loggedInUser.getShopCode())
                .user(loggedInUser)
                .shop(loggedInUser.getShop())
                .build();
        expenseRepository.save(salaryExpense);

        return new PaymentResponseDto("Salary payment successful",
                salaryAmount, employee.getName(), LocalDate.now());
    }

    @Transactional
    public StockPurchase addStockPurchase(Long productId, StockPurchase stockPurchase) {
        User loggedInUser = getLoggedInUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setStock(product.getStock() + stockPurchase.getQuantity());
        product.setPrice(stockPurchase.getTotalCost() + (stockPurchase.getQuantity() * stockPurchase.getItemCost()));
        productRepository.save(product);
        stockPurchase.setTotalCost(stockPurchase.getQuantity() * stockPurchase.getItemCost());

        stockPurchase.setProduct(product);
        stockPurchase.setUser(loggedInUser);
        stockPurchase.setPurchaseDate(LocalDate.now());
        stockPurchase.setShop(loggedInUser.getShop());
        stockPurchaseRepository.save(stockPurchase);

        Expense stockExpense = Expense.builder()
                .expenseType(ExpenseType.STOCKPURCHASE.name())
                .amount(stockPurchase.getTotalCost())
                .shopCode(loggedInUser.getShopCode())
                .description(product.getName()+""+ExpenseType.STOCKPURCHASE.name())
                .date(LocalDate.now())
                .user(loggedInUser)
                .shop(loggedInUser.getShop())
                .build();
        expenseRepository.save(stockExpense);

        return stockPurchase;
    }

    // Handle investment
    public Investment addInvestment(Investment investment) {
        User loggedInUser = getLoggedInUser();
        investment.setDate(LocalDate.now());
        investment.setShopCode(loggedInUser.getShopCode());
        investment.setShop(loggedInUser.getShop());
        investment.setUser(loggedInUser);
        return investmentRepository.save(investment);
    }

    public double calculateGrossProfit() {
        User loggedInUser = getLoggedInUser();
        double totalRevenue = saleRepository.findByShop(loggedInUser.getShop()).stream()
                .mapToDouble(sale -> sale.getSalePrice() * sale.getQuantity()).sum();

        double totalStockCosts = saleRepository.findByShop(loggedInUser.getShop()).stream()
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
    public List<Expense> getExpensesByExpenseType(ExpenseType expenseType) {
        return null;
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

    public List<Expense> findExpensesByType(ExpenseType expenseType) {
        User loggedInUser = getLoggedInUser();
        return expenseRepository.findByShopAndExpenseType(loggedInUser.getShop(), expenseType.name());
    }
}
