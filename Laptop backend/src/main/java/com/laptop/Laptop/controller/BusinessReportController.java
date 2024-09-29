package com.laptop.Laptop.controller;

import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.entity.Sale;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.services.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class BusinessReportController {

    @Autowired
    private BusinessService businessService;



    @GetMapping("/sales")
    public ResponseEntity<List<Sale>> getSalesForPeriod(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<Sale> sales = businessService.getSalesByDateRange(startDate, endDate);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@RequestParam ExpenseType expenseType) {
        List<Expense> expenses = businessService.getExpensesByExpenseType(expenseType);
        return ResponseEntity.ok(expenses);
    }

//    @GetMapping("/stock-flow")
//    public ResponseEntity<List<Product>> getStockFlow() {
//        List<Product> stockFlow = businessService.getStockFlow();
//        return ResponseEntity.ok(stockFlow);
//    }

    @GetMapping("/top-products")
    public ResponseEntity<List<Product>> getTopProducts() {
        List<Product> topProducts = businessService.getTopSellingProducts();
        return ResponseEntity.ok(topProducts);
    }
}
