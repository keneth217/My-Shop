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



    public Sale createSale(List<Long> productIds, List<Integer> quantities, Sale sale) {
        User loggedInUser = getLoggedInUser();
        String salePerson = loggedInUser.getUsername();
        double totalPrice = 0;

        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            int quantity = quantities.get(i);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));

            if (product.getStock() < quantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            // Deduct stock and update product
            product.setStock(product.getStock() - quantity);
            product.setQuantitySold(product.getQuantitySold() + quantity);
            productRepository.save(product);

            // Create SaleItem for each product
            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setQuantity(quantity);
            saleItem.setSalePrice(product.getPrice());
            saleItem.setSale(sale);  // Set sale reference here

            sale.getSaleItems().add(saleItem);

            // Add to total price
            totalPrice += product.getPrice() * quantity;

            // Set the first product for the Sale entity (legacy support)
            if (i == 0) {
                sale.setProduct(product);  // Set first product for the Sale
            }
        }

        // Set other details of the sale
        sale.setShopCode(loggedInUser.getShop().getShopCode());
        sale.setDate(LocalDate.now());
        sale.setSalePerson(salePerson);
        sale.setUser(loggedInUser);
        sale.setShop(loggedInUser.getShop());
        sale.setTotalPrice(totalPrice);

        // Save the sale along with its items
        return saleRepository.save(sale);
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
        product.setStock(product.getStock() + stockPurchase.getQuantity());
        product.setPrice(stockPurchase.getTotalCost() + (stockPurchase.getQuantity() * stockPurchase.getItemCost()));
        productRepository.save(product);

        // Set the total cost of the stock purchase
        stockPurchase.setTotalCost(stockPurchase.getQuantity() * stockPurchase.getItemCost());

        // Set relationships and other fields
        stockPurchase.setProduct(product);
        stockPurchase.setUser(loggedInUser);

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

        double totalStockCosts = saleRepository.findByShopIdAndShopCode(shopId, shopCode).stream()
                .mapToDouble(sale -> sale.getProduct().getCost() * sale.getQuantity()).sum();

        return totalRevenue - totalStockCosts;
    }

    @Transactional
    public Sale createSale(Long productId, Sale sale) throws IOException, java.io.IOException {
        User loggedInUser = getLoggedInUser();
        String salePerson = getLoggedInUser().getUsername();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStock() < sale.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock available.");
        }

        // Deduct the sold quantity from the product's stock
        product.setStock(product.getStock() - sale.getQuantity());

        // Update product total price and quantity sold
        product.setPrice(product.getPrice() + (sale.getSalePrice() * sale.getQuantity()));
        product.setQuantitySold(product.getQuantitySold() + sale.getQuantity());
        productRepository.save(product);

        // Set shop and sale details
        sale.setShopCode(loggedInUser.getShop().getShopCode());
        sale.setProduct(product);
        sale.setSaleTotal(sale.getQuantity() * sale.getSalePrice());
        sale.setDate(LocalDate.now());
        sale.setSalePerson(salePerson);
        sale.setUser(loggedInUser);
        sale.setShop(loggedInUser.getShop());

        // Save the sale
        Sale savedSale = saleRepository.save(sale);
        return savedSale;
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
    public PaymentResponseDto payEmployee(Long employeeId, SalaryDto salary) {
        User loggedInUser = getLoggedInUser();

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalStateException("Employee not found"));

        if (!employee.getShop().getId().equals(loggedInUser.getShop().getId())) {
            throw new IllegalStateException("Unauthorized to pay this employee");
        }

        Payment salaryPayment = Payment.builder()
                .employee(employee)
                .amount(salary.getSalaryAmount())
                .paymentDate(LocalDate.now())
                .build();
        paymentRepository.save(salaryPayment);

        Expense salaryExpense = Expense.builder()
                .expenseType(ExpenseType.SALARY.name())
                .amount(salary.getSalaryAmount())
                .date(LocalDate.now())
                .shopCode(loggedInUser.getShopCode())
                .user(loggedInUser)
                .shop(loggedInUser.getShop())
                .build();
        expenseRepository.save(salaryExpense);

        return new PaymentResponseDto("Salary payment successful",
                salary.getSalaryAmount(), employee.getName(), LocalDate.now());
    }


    // Handle investment
    public Investment addInvestment(Investment investment) {
        User loggedInUser = getLoggedInUser();
        System.out.println(loggedInUser);
        System.out.println("--------------addding investement------------");
        investment.setDate(LocalDate.now());
        investment.setShopCode(loggedInUser.getShopCode());
        investment.setShop(loggedInUser.getShop());
        investment.setUser(loggedInUser);
        investment.setDescription("Initial investement");
        investment.setAmount(investment.getAmount());
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



    // Method to fetch sales for a shop within a date range
    public List<Sale> getSalesForShop(Long shopId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByShopIdAndDateBetween(shopId, startDate, endDate);
    }

    // Method to fetch stock purchases for a shop within a date range
    public List<StockPurchase> getStockPurchasesForShop(Long shopId, LocalDate startDate, LocalDate endDate) {
        return stockPurchaseRepository.findByShopIdAndPurchaseDateBetween(shopId, startDate, endDate);
    }


    public Supplier addSupplier(Supplier supplier) {
        User loggedInUser = getLoggedInUser();

        // Set shop and shop code for the supplier
        supplier.setShop(loggedInUser.getShop());
        supplier.setShopCode(loggedInUser.getShopCode());

        return supplierRepository.save(supplier);
    }

    @Transactional
    public Cart addToCart(Long productId, int quantity, User user) {

        // Get the logged-in user (assumed to be the same as the provided user)
        User loggedInUser = getLoggedInUser();

        // Find the cart for the logged-in user or create a new one
        Cart cart = cartRepository.findByUser(loggedInUser)
                .orElse(new Cart(loggedInUser)); // Create a new cart for the logged-in user if not found

        // Find the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Check if the product is already in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // If the product already exists in the cart, update the quantity
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            // If the product is not in the cart, create a new CartItem
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);

            // Set shop details from the logged-in user
            cartItem.setShop(loggedInUser.getShop()); // Set the shop
            cartItem.setShopCode(loggedInUser.getShop().getShopCode()); // Set the shop code

            // Set the logged-in user for the CartItem
            cartItem.setUser(loggedInUser);

            // Add the new CartItem to the cart
            cart.getItems().add(cartItem);
        }

        // Save and return the updated cart
        return cartRepository.save(cart);
    }

    // Checkout the cart for the logged-in user, ensuring the cart belongs to their shop
    @Transactional
    public Sale checkoutCart(User user, String customerName, String customerPhone) throws java.io.IOException {
        User loggedInUser = getLoggedInUser();

        // Fetch cart for the logged-in user's shop
        Cart cart = cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for the logged-in user's shop"));

        double totalPrice = 0;
        Sale sale = new Sale(); // Create new sale object

        // Set sale details before saving
        sale.setShopCode(loggedInUser.getShop().getShopCode());
        sale.setDate(LocalDate.now());
        sale.setSalePerson(loggedInUser.getUsername());
        sale.setUser(loggedInUser);  // Associate with the logged-in user
        sale.setShop(loggedInUser.getShop());
        sale.setCustomerName(customerName);
        sale.setCustomerPhone(customerPhone);

        // Save sale before adding sale items
        Sale savedSale = saleRepository.save(sale);

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            if (product.getStock() < quantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            // Deduct stock and update product
            product.setStock(product.getStock() - quantity);
            product.setQuantitySold(product.getQuantitySold() + quantity);
            productRepository.save(product);

            // Create SaleItem for each product
            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setQuantity(quantity);
            saleItem.setSalePrice(product.getPrice());
            saleItem.setSale(savedSale);  // Associate SaleItem with the saved sale

            savedSale.getSaleItems().add(saleItem);  // Add SaleItem to Sale

            // Add to total price
            totalPrice += product.getPrice() * quantity;
        }

        // Update total price after adding items
        savedSale.setTotalPrice(totalPrice);
        saleRepository.save(savedSale);  // Save sale again with updated total price

        // Generate and save receipt for later reference
        ByteArrayInputStream receiptStream = pdfReportServices.generateReceiptForSale(savedSale.getId());

        // Optionally, send the receipt via email or another method

        // Clear the cart after checkout
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedSale;
    }

    // Fetch cart items for the logged-in user specific to their shop
    @Transactional(readOnly = true)
    public Cart getCartItems(User user) {
        User loggedInUser = getLoggedInUser();

        // Ensure the cart returned is specific to the logged-in user's shop
        return cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElse(new Cart(loggedInUser));  // If no cart exists for the user, return an empty cart
    }



}
