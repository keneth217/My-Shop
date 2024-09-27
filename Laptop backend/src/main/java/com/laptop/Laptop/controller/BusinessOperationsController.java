package com.laptop.Laptop.controller;

import com.laptop.Laptop.entity.Employee;
import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Sale;
import com.laptop.Laptop.entity.StockPurchase;
import com.laptop.Laptop.services.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
public class BusinessOperationsController {

    @Autowired
    private BusinessService businessService;

    // Create a new sale linked to a product
    @PostMapping("/sales/{productId}")
    public ResponseEntity<Sale> createSale(@PathVariable Long productId, @RequestBody Sale sale) {
        Sale createdSale = businessService.createSale(productId, sale);
        return ResponseEntity.ok(createdSale);
    }

    // Add a new expense
    @PostMapping("/expenses")
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        Expense addedExpense = businessService.addExpense(expense);
        return ResponseEntity.ok(addedExpense);
    }

    // Add a new employee and automatically track their salary as an expense
    @PostMapping("/employees")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        Employee addedEmployee = businessService.addEmployee(employee);
        return ResponseEntity.ok(addedEmployee);
    }

    // Add a new stock purchase linked to a product
    @PostMapping("/stock-purchases/{productId}")
    public ResponseEntity<StockPurchase> addStockPurchase(@PathVariable Long productId, @RequestBody StockPurchase stockPurchase) {
        StockPurchase addedStockPurchase = businessService.addStockPurchase(productId, stockPurchase);
        return ResponseEntity.ok(addedStockPurchase);
    }
}
