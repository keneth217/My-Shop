package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.SupplierDto;
import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.services.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierDto> addSupplier(@RequestBody SupplierDto supplier) {
        SupplierDto savedSupplier = supplierService.addSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSupplier);
    }
    @GetMapping
    public ResponseEntity<List<SupplierDto>> getSuppliers(){
        List<SupplierDto> suppliers=supplierService.getSuppliersForShop();
        if (suppliers.isEmpty()){
            return  ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(suppliers);
    }
}
