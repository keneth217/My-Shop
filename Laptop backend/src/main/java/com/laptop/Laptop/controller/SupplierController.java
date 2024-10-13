package com.laptop.Laptop.controller;

import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.services.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ResponseEntity<Supplier> addSupplier(@RequestBody Supplier supplier) {
        Supplier savedSupplier = supplierService.addSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSupplier);
    }
}
