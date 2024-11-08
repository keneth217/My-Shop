package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.InvestmentDto;
import com.laptop.Laptop.dto.StockPurchaseDto;
import com.laptop.Laptop.dto.SupplierDto;
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
import java.time.YearMonth;
import java.util.HashMap;
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
    private SupplierRepository supplierRepository;
    @Autowired
    private InvestmentRepository investmentRepository;

    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final int OUT_OF_STOCK_THRESHOLD = 0;

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
    public StockPurchaseDto addStockPurchase(Long productId, Long supplierId, StockPurchaseDto stockPurchaseDto) {
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
        product.setCost(stockPurchaseDto.getBuyingPrice());
        product.setSellingPrice(stockPurchaseDto.getSellingPrice());
        product.setStock(product.getStock() + stockPurchaseDto.getQuantity());

        productRepository.save(product);

        double stockCost = stockPurchaseDto.getQuantity() * stockPurchaseDto.getBuyingPrice();

        // Create and set stock purchase details
        StockPurchase stockPurchase = StockPurchase.builder()
                .totalCost(stockCost)
                .product(product)
                .supplier(supplier)  // Assign the supplier to stock purchase
                .purchaseDate(LocalDate.now())
                .user(loggedInUser)
                .shopCode(loggedInUser.getShopCode())
                .shop(userShop)
                .supplierName(supplier.getSupplierName()) // Set supplier name here
                .quantity(stockPurchaseDto.getQuantity())
                .buyingPrice(stockPurchaseDto.getBuyingPrice())
                .sellingPrice(stockPurchaseDto.getSellingPrice())
                .build();

        stockPurchaseRepository.save(stockPurchase);

        // Log stock purchase as an expense
        Expense expense = new Expense();
        expense.setType(ExpenseType.STOCKPURCHASE);
        expense.setAmount(stockCost);
        expense.setDescription(product.getName() + " - Stock Purchase");
        expense.setDate(LocalDate.now());
        expense.setUser(loggedInUser);
        expense.setShop(userShop);
        expense.setShopCode(loggedInUser.getShopCode());

        expenseRepository.save(expense);

        // Prepare and return StockPurchaseDto
        return StockPurchaseDto.builder()
                .purchaseDate(stockPurchase.getPurchaseDate())
                .quantity(stockPurchase.getQuantity())
                .buyingPrice(stockPurchase.getBuyingPrice())
                .sellingPrice(stockPurchase.getSellingPrice())
                .totalCost(stockCost)
                .productName(stockPurchase.getProduct().getName())
                .supplierName(stockPurchase.getSupplierName())  // Assuming Supplier has a getSupplierName() method
                .shopCode(userShop.getShopCode())
                .build();
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
            .mapToDouble(purchase -> purchase.getProduct().getCost() * purchase.getQuantity())
            .sum();

    // Gross Profit = Total Revenue - Total Stock Cost
    return totalRevenue - totalStockCost;
}
@Override
public double calculateNetProfit() {
    // Calculate gross profit for the user's shop
    double grossProfit = calculateGrossProfit();
    
    // Calculate total expenses
    double totalExpenses = getTotalExpenses();

    // Calculate total investments (money injected into the business)
    double totalInvestments = getTotalInvestments();

    // Real Net Profit = Gross Profit - Total Expenses - Total Investments
    return grossProfit - totalExpenses - totalInvestments;
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

    @Override
    public Page<Product> getOutOFStockAlerts(Pageable pageable) {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return productRepository.findAllByShopAndStockLessThanEqual(shop, OUT_OF_STOCK_THRESHOLD, pageable);
    }

    public Page<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return saleRepository.findAllByShopAndDateBetween(shop, startDate, endDate, pageable);
    }

    public Page<Sale> getSalesByYearMonthAndDateRange(int year, int month, LocalDate rangeStart, LocalDate rangeEnd, Pageable pageable) {
        Shop shop = getUserShop();  // Get the logged-in user's shop

        // Define the start and end of the selected month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        // Adjust startDate and endDate based on the given date range, constrained within the month
        LocalDate startDate = (rangeStart != null && !rangeStart.isBefore(monthStart)) ? rangeStart : monthStart;
        LocalDate endDate = (rangeEnd != null && !rangeEnd.isAfter(monthEnd)) ? rangeEnd : monthEnd;
        System.out.println("getting sales.......................... for" +yearMonth);
        return saleRepository.findAllByShopAndDateBetween(shop, startDate, endDate, pageable);
    }
    public Page<Sale> getSalesByYearAndMonth(int year, int month, Pageable pageable) {
        Shop shop = getUserShop();  // Get the logged-in user's shop

        // Define the start and end of the selected month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        System.out.println("Getting sales for " + yearMonth);
        // Query for all sales in the specified month and year for the shop
        return saleRepository.findAllByShopAndDateBetween(shop, monthStart, monthEnd, pageable);
    }



    public Page<Expense> getExpensesByType(ExpenseType type, Pageable pageable) {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return expenseRepository.findAllByType(shop, type, pageable);
    }

    public List<Sale> getSalesForShop(LocalDate startDate, LocalDate endDate) {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return saleRepository.findByShopAndDateBetween(shop, startDate, endDate);
    }

    public List<StockPurchaseDto> getStockPurchasesForShop(LocalDate startDate, LocalDate endDate) {


        Shop shop = getUserShop();  // Get the logged-in user's shop

        List<StockPurchase> stockPurchases=stockPurchaseRepository.findByShopAndPurchaseDateBetween(shop, startDate, endDate);
        return stockPurchases.stream()
                .map(this::convertToDto) // Convert each Investment to InvestmentDto
                .collect(Collectors.toList());
    }

    // Method to get stock purchases by shop and supplier name
    public List<StockPurchaseDto> getStockPurchasesForShopBySupplierName(LocalDate startDate, LocalDate endDate, Long supplierId) {
        Shop shop = getUserShop(); // Get the logged-in user's shop

        // Find stock purchases by shop, supplier name, and date range
        List<StockPurchase> stockPurchases = stockPurchaseRepository.findByShopAndSupplierIdAndPurchaseDateBetween(shop, supplierId, startDate, endDate);

        // Convert stock purchases to DTOs
        return stockPurchases.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private StockPurchaseDto convertToDto(StockPurchase stockPurchase) {
        return StockPurchaseDto.builder()
                .stockBy(stockPurchase.getUser().getUsername())
                .id(stockPurchase.getId())
                .productName(stockPurchase.getProduct().getName())
                .buyingPrice(stockPurchase.getBuyingPrice())
                .sellingPrice(stockPurchase.getSellingPrice())
                .purchaseDate(stockPurchase.getPurchaseDate())
                .shopCode(stockPurchase.getSupplierName())
                .quantity(stockPurchase.getQuantity())
                .totalCost(stockPurchase.getTotalCost())
                .buyingPrice(stockPurchase.getBuyingPrice())
                .shopCode(stockPurchase.getShopCode())
                .build();
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

        // Ensure that the request returns a maximum of 10 products per page
        Pageable limitedPageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());

        // Fetch top-selling products for this shop with quantitySold > 0
        return productRepository.findTopSellingProductsByShop(shop.getId(), limitedPageable);
    }

}
