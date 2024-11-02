package com.laptop.Laptop.controller;

import com.laptop.Laptop.constants.MyConstants;
import com.laptop.Laptop.dto.ExpenseDto;
import com.laptop.Laptop.dto.Responses.Responsedto;
import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.services.ExpenseServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {
@Autowired
private ExpenseServices expenseServices;


    // Add a new expense
    @PostMapping("/add")
    public ResponseEntity<Responsedto> addExpense(@RequestBody ExpenseDto expense) {
        ExpenseDto savedExpense = expenseServices.addExpense(expense);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(MyConstants.EXPENSE_CREATION,MyConstants.EXPENSE_CREATION_CODE));
    }
    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getSuppliers(){
        List<ExpenseDto> expense=expenseServices.getExpenseForShop();
        if (expense.isEmpty()){
            return  ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(expense);
    }

    @GetMapping("/totals")
    public ResponseEntity<Map<ExpenseType, Double>> getExpenseTotals() {
        Map<ExpenseType, Double> totals = expenseServices.getExpenseTotal();
        return ResponseEntity.ok(totals);
    }



}
