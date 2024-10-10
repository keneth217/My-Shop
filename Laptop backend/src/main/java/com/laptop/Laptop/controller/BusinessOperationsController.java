package com.laptop.Laptop.controller;

import com.laptop.Laptop.constants.AuthConstants;
import com.laptop.Laptop.constants.MyConstants;
import com.laptop.Laptop.dto.*;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.services.BusinessService;
import com.laptop.Laptop.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/business")
public class BusinessOperationsController {

    @Autowired
    private BusinessService businessService;

//    @PostMapping("/create")
//    public ResponseEntity<?> createSale(@RequestBody SaleRequest saleRequest) {
//        Sale sale = businessService.createSale(saleRequest.getProductIds(), saleRequest.getQuantities(), new Sale());
//        return ResponseEntity.ok(sale);
//    }

    @PostMapping("/create")
    public ResponseEntity<Sale> createSale(
            @RequestBody SaleRequest saleRequest) {
        List<Long> productIds = saleRequest.getProductIds();
        List<Integer> quantities = saleRequest.getQuantities();
        Sale sale = new Sale();

        Sale savedSale = businessService.createSale(productIds, quantities, sale);

        return ResponseEntity.ok(savedSale);
    }

    // Create a new sale linked to a product
    @PostMapping("/sales/{productId}")
    public ResponseEntity<Responsedto> createSale(@PathVariable Long productId, @RequestBody Sale sale) throws IOException {
        businessService.createSale(productId, sale);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(MyConstants.PRODUCT_SALE_CODE,MyConstants.PRODUCT_SALE_MESSAGE));
    }

    // Add a new expense
    @PostMapping("/expenses")
    public ResponseEntity<Responsedto> addExpense(@RequestBody Expense expense) {
        Expense addedExpense = businessService.addExpense(expense);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(MyConstants.EXPENSE_CREATION,MyConstants.EXPENSE_CREATION_CODE));
    }
    @GetMapping("/sales")
    public ResponseEntity<PaginatedResponse<Sale>> getSalesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Pageable pageable) {
        Page<Sale> salePage = businessService.getSalesByDateRange(startDate, endDate, pageable);
        PaginatedResponse<Sale> response = new PaginatedResponse<>(salePage);
        return ResponseEntity.ok(response);
    }

    // Add a new employee and automatically track their salary as an expense
    @PostMapping("/employees")
    public ResponseEntity<Responsedto> addEmployee(@RequestBody Employee employee) {
        Employee addedEmployee = businessService.addEmployee(employee);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body( new Responsedto(MyConstants.EMPLOYEE_CREATION,MyConstants.EMPLOYEE_CREATION_CODE));// Use 201 Created
    }

    @PostMapping("/invest")
    public ResponseEntity<Investment> addInvestement(@RequestBody Investment investment ) {
        Investment saveInvestement = businessService.addInvestment(investment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saveInvestement);
    }
    @PostMapping("/supplier")
    public ResponseEntity<Supplier> addSupplier(@RequestBody Supplier supplier) {
        Supplier savedSupplier = businessService.addSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSupplier);
    }

    // Endpoint for paying employee salary
    @PostMapping("/{employeeId}/pay")
    public ResponseEntity<PaymentResponseDto> payEmployee(
            @PathVariable Long employeeId,
            @RequestBody SalaryDto salary) {

        // Call the service to handle the salary payment
        PaymentResponseDto response = businessService.payEmployee(employeeId, salary);

        // Return the response with HTTP 200 OK status
        return ResponseEntity.ok(response);
    }

    // Add a new stock purchase linked to a product
    @PostMapping("/stock-purchases/{productId}/{supplierId}")
    public ResponseEntity<Responsedto> addStockPurchase(@PathVariable Long productId,@PathVariable Long supplierId, @RequestBody StockPurchase stockPurchase) {
        StockPurchase addedStockPurchase = businessService.addStockPurchase(productId,supplierId, stockPurchase);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(MyConstants.STOCK_PURCHASE_CODE,MyConstants.STOCK_PURCHASE_CREATION));
    }
    @GetMapping("/top-products")
    public ResponseEntity<PaginatedResponse<Product>> getTopProducts(Pageable pageable) {
        Page<Product> topProductsPage = businessService.getTopSellingProducts(pageable);
        PaginatedResponse<Product> response = new PaginatedResponse<>(topProductsPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-alerts")
    public ResponseEntity<PaginatedResponse<Product>> getStockAlerts( Pageable pageable) {
        Page<Product> stockAlerts = businessService.getStockAlerts(pageable);
        PaginatedResponse<Product> response = new PaginatedResponse<>(stockAlerts);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/sale/{shopId}/{shopCode}")
    public ResponseEntity<Double> getTotalSales(@PathVariable  Long shopId,String shopCode) {
        double sales = businessService.getTotalSalesForShop(shopId,shopCode);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/net-profit/{shopId}/{shopCode}")
    public ResponseEntity<Double> getNetProfit(@PathVariable  Long shopId,String shopCode) {
        double netProfit = businessService.calculateNetProfitForShop(shopId,shopCode);
        return ResponseEntity.ok(netProfit);
    }

    @GetMapping("/gross-profit/{shopId}/{shopCode}")
    public ResponseEntity<Double> getGrossProfit(@PathVariable  Long shopId,String shopCode) {
        double grossProfit = businessService.calculateGrossProfitForShop(shopId,shopCode);
        return ResponseEntity.ok(grossProfit);
    }
}
