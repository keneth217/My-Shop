package com.laptop.Laptop.controller;

import com.laptop.Laptop.entity.Investment;
import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.services.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invest")
public class InvestmentController {
    @Autowired
    private InvestmentService investmentService;

    @PostMapping
    public ResponseEntity<Investment> addInvestment(@RequestBody Investment investment ) {
        Investment saveInvestment = investmentService.addInvestment(investment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saveInvestment);
    }

    @GetMapping
    public ResponseEntity<List<Investment>> getSuppliers(){
        List<Investment> investment=investmentService.getInvestementForShop();
        if (investment.isEmpty()){
            return  ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(investment);
    }
}
