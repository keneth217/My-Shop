package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.enums.ExpenseType;

import com.laptop.Laptop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private static final int LOW_STOCK_THRESHOLD = 5;

    // Utility: Get the current logged-in user
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Assumes User implements UserDetails
    }

    // Fetch current user’s shop
    private Shop getUserShop() {
        return getLoggedInUser().getShop();
    }

    @Transactional
    public StockPurchase addStockPurchase(Long productId, Long supplierId, StockPurchase stockPurchase) {
        User loggedInUser = getLoggedInUser();
        Shop userShop = loggedInUser.getShop();

        // Ensure the product belongs to the user's shop
        Product product = productRepository.findById(productId)
                .filter(p -> p.getShop().getId().equals(userShop.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Product not found or not part of your shop"));

        // Find supplier
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        // Update product stock and cost
        product.setCost(stockPurchase.getItemCost());
        product.setStock(product.getStock() + stockPurchase.getQuantity());
        product.setPrice(product.getStock() * stockPurchase.getItemCost());

        productRepository.save(product);

        // Set stock purchase details
        stockPurchase.setTotalCost(stockPurchase.getQuantity() * stockPurchase.getItemCost());
        stockPurchase.setProduct(product);
        stockPurchase.setSupplier(supplier);
        stockPurchase.setPurchaseDate(LocalDate.now());
        stockPurchase.setUser(loggedInUser);
        stockPurchase.setShop(userShop);

        stockPurchaseRepository.save(stockPurchase);

        // Log stock purchase as expense
        Expense expense = new Expense();
        expense.setType(ExpenseType.STOCKPURCHASE);
        expense.setAmount(stockPurchase.getTotalCost());
        expense.setDescription(product.getName() + " - Stock Purchase");
        expense.setDate(LocalDate.now());
        expense.setUser(loggedInUser);
        expense.setShop(userShop);

        expenseRepository.save(expense);
        return stockPurchase;
    }

    @Override
    public long totalUsersByShop() {
        return userRepository.countByShop(getUserShop());
    }

    @Override
    public int totalEmployees() {
        return (int) employeeRepository.countByShop(getUserShop());
    }

    @Override
    public int totalProducts() {
        return (int) productRepository.countByShop(getUserShop());
    }

    @Override
    public double calculateGrossProfit() {
        Shop shop = getUserShop();  // Get the shop of the logged-in user

        // Calculate total revenue from sales
        double totalRevenue = saleRepository.findByShop(shop).stream()
                .mapToDouble(Sale::getTotalPrice)
                .sum();

        // Calculate total stock cost from purchases
        double totalStockCost = stockPurchaseRepository.findByShop(shop).stream()
                .mapToDouble(purchase ->
                        purchase.getProduct().getCost() * purchase.getQuantity()
                ).sum();

        // Gross Profit = Total Revenue - Total Stock Cost
        return totalRevenue - totalStockCost;
    }

    @Override
    public double calculateNetProfit() {
        // Calculate gross profit for the user's shop
        double grossProfit = calculateGrossProfit();
        double totalExpenses = getTotalExpenses();  // Get total expenses
        double totalInvestments = getTotalInvestments();  // Get total investments

        // Net Profit = Gross Profit - Total Expenses + Total Investments
        return grossProfit - totalExpenses + totalInvestments;
    }

    public double getTotalSales() {
        Shop shop = getUserShop();  // Get the shop of the logged-in user
        return saleRepository.findByShop(shop).stream()
                .mapToDouble(Sale::getTotalPrice)
                .sum();
    }

    public double getTotalExpenses() {
        Shop shop = getUserShop();  // Get the shop of the logged-in user
        return expenseRepository.findByShop(shop).stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double getTotalInvestments() {
        Shop shop = getUserShop();  // Get the shop of the logged-in user
        return investmentRepository.findByShop(shop).stream()
                .mapToDouble(Investment::getAmount)
                .sum();
    }
    @Override
    public Page<Product> getStockAlerts(Pageable pageable) {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return productRepository.findAllByShopAndStockLessThanEqual(shop, LOW_STOCK_THRESHOLD, pageable);
    }

    public Page<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return saleRepository.findAllByShopAndDateBetween(shop, startDate, endDate, pageable);
    }

    public Page<Expense> getExpensesByType(ExpenseType type, Pageable pageable) {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return expenseRepository.findAllByType(shop, type, pageable);
    }

    public List<Sale> getSalesForShop(LocalDate startDate, LocalDate endDate) {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return saleRepository.findByShopAndDateBetween(shop, startDate, endDate);
    }

    public List<StockPurchase> getStockPurchasesForShop(LocalDate startDate, LocalDate endDate) {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return stockPurchaseRepository.findByShopAndPurchaseDateBetween(shop, startDate, endDate);
    }

    @Override
    public double getTotalSalesForShop(Long shopId, String shopCode) {
        // Get total sales for a specific shop based on shop ID and code
        return saleRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(Sale ::getTotalPrice).sum();
    }

    @Override
    public double calculateNetProfitForShop(Long shopId, String shopCode) {
        // Calculate the gross profit for the specific shop
        double grossProfit = calculateGrossProfitForShop(shopId, shopCode);

        // Sum the total expenses for the shop
        double totalExpenses = expenseRepository.findByShopIdAndShopCode(shopId, shopCode)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // Sum the total investments for the shop
        double totalInvestments = investmentRepository.findByShopIdAndShopCode(shopId, shopCode)
                .stream()
                .mapToDouble(Investment::getAmount)
                .sum();

        // Calculate and return the net profit
        return grossProfit - totalExpenses + totalInvestments;
    }

    @Override
    public double calculateGrossProfitForShop(Long shopId, String shopCode) {
        // Calculate the gross profit for a specific shop
        double totalRevenue = saleRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(Sale :: getTotalPrice).sum();

        double totalStockCosts = stockPurchaseRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(s -> s.getProduct().getCost() * s.getQuantity())
                .sum();

        return totalRevenue - totalStockCosts;


    }

    public Page<Product> getTopSellingProducts(Pageable pageable) {
        // Get the shop associated with the logged-in user
        Shop shop = getUserShop();

        // Ensure that the request always returns only 10 products per page
        Pageable limitedPageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());

        // Fetch top-selling products for this shop
        return productRepository.findTopSellingProductsByShop(shop.getId(), limitedPageable);
    }

}
