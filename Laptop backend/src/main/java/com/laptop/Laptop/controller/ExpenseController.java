package com.laptop.Laptop.controller;

import com.laptop.Laptop.constants.MyConstants;
import com.laptop.Laptop.dto.Responses.Responsedto;
import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.services.ExpenseServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {
@Autowired
private ExpenseServices expenseServices;


    // Add a new expense
    @PostMapping("/add")
    public ResponseEntity<Responsedto> addExpense(@RequestBody Expense expense) {
        Expense savedExpense = expenseServices.addExpense(expense);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(MyConstants.EXPENSE_CREATION,MyConstants.EXPENSE_CREATION_CODE));
    }
    @GetMapping
    public ResponseEntity<List<Expense>> getSuppliers(){
        List<Expense> expense=expenseServices.getExpenseForShop();
        if (expense.isEmpty()){
            return  ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(expense);
    }



}
