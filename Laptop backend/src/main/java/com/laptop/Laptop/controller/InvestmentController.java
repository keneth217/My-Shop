package com.laptop.Laptop.controller;

import com.laptop.Laptop.entity.Investment;
import com.laptop.Laptop.services.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
