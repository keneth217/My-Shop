package com.laptop.Laptop.controller;

import com.laptop.Laptop.constants.MyConstants;
import com.laptop.Laptop.dto.Responsedto;
import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.services.ExpenseServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
