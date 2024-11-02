package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.InvestmentDto;
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
    public ResponseEntity<InvestmentDto> addInvestment(@RequestBody InvestmentDto investmentDto) {
        InvestmentDto savedInvestment = investmentService.addInvestment(investmentDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedInvestment);
    }

    @GetMapping
    public ResponseEntity<List<InvestmentDto>> getInvestments() {
        List<InvestmentDto> investments = investmentService.getInvestmentForShop();
        if (investments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(investments);
    }
}
