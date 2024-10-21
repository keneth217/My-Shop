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
    public ResponseEntity<Responsedto> addStockPurchase(@PathVariable Long productId,@PathVariable Long supplierId, @RequestBody StockPurchase stockPurchase) {
        StockPurchase addedStockPurchase = businessService.addStockPurchase(productId,supplierId, stockPurchase);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(MyConstants.STOCK_PURCHASE_CODE,MyConstants.STOCK_PURCHASE_CREATION));
    }
//    @GetMapping("/top-products")
//    public ResponseEntity<PaginatedResponse<Product>> getTopProducts(Pageable pageable) {
//        Page<Product> topProductsPage = businessService.getTopSellingProducts(pageable);
//        PaginatedResponse<Product> response = new PaginatedResponse<>(topProductsPage);
//        return ResponseEntity.ok(response);
//    }

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
