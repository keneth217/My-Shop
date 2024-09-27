package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.ProductCreationRequestDto;
import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody ProductCreationRequestDto request, HttpServletRequest httpRequest) {
        // Extract shopId from the request attributes set by the JWT filter
        Long shopId = (Long) httpRequest.getAttribute("shopId");

        // Add the new product under the same shop
        Product newProduct = productService.addProductToShop(shopId, request);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(HttpServletRequest request) {
        // Extract shopId from request, which was added by JwtAuthenticationFilter
        Long shopId = (Long) request.getAttribute("shopId");

        List<Product> products = productService.getProductsForShop(shopId);
        return ResponseEntity.ok(products);
    }
}
