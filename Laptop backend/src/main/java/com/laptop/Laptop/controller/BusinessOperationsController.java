package com.laptop.Laptop.controller;

import com.laptop.Laptop.constants.MyConstants;
import com.laptop.Laptop.dto.*;
import com.laptop.Laptop.dto.Responses.Responsedto;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.services.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/business")
public class BusinessOperationsController {

    @Autowired
    private BusinessService businessService;

    @GetMapping("/sales")
    public ResponseEntity<PaginatedResponse<Sale>> getSalesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Pageable pageable) {
        Page<Sale> salePage = businessService.getSalesByDateRange(startDate, endDate, pageable);
        PaginatedResponse<Sale> response = new PaginatedResponse<>(salePage);
        return ResponseEntity.ok(response);
    }


    // Add a new stock purchase linked to a product
    @PostMapping("/stock-purchases/{productId}/{supplierId}")
    public ResponseEntity<Responsedto> addStockPurchase(@PathVariable Long productId, @PathVariable Long supplierId, @RequestBody StockPurchaseDto stockPurchase) {
        StockPurchaseDto addedStockPurchase = businessService.addStockPurchase(productId,supplierId, stockPurchase);
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
    @GetMapping("/daily-sales")
    public ResponseEntity<Page<Sale>> getSalesByYearMonthAndDateRange(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate rangeStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate rangeEnd,
            Pageable pageable) {

        Page<Sale> sales = businessService.getSalesByYearMonthAndDateRange(year, month, rangeStart, rangeEnd, pageable);
        return ResponseEntity.ok(sales);
    }

    // Endpoint to retrieve sales by year and month
    @GetMapping("/monthly-sales")
    public ResponseEntity<Page<Sale>> getSalesByYearAndMonth(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Sale> sales = businessService.getSalesByYearAndMonth(year, month, pageable);

        return ResponseEntity.ok(sales);
    }

}
