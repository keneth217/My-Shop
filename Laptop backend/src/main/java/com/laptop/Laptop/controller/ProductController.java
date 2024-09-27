package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.ProductCreationRequestDto;
import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody ProductCreationRequestDto request, HttpServletRequest httpRequest) {
        // Extract shopId from the request attributes set by the JWT filter
        Long shopId = (Long) httpRequest.getAttribute("shopId");

        // Add the new product under the same shop
        Product newProduct = productService.addProductToShop(shopId, request);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductCreationRequestDto>> getProducts(HttpServletRequest request) {
        Long shopId = (Long) request.getAttribute("shopId");

        if (shopId == null) {
            logger.warn("Shop ID is null for request: {}", request);
            return ResponseEntity.badRequest().body(null);
        }

        List<ProductCreationRequestDto> products = productService.getProductsForShop(shopId);

        if (products.isEmpty()) {
            logger.info("No products found for shop ID: {}", shopId);
            return ResponseEntity.noContent().build();
        }

        logger.info("Retrieved {} products for shop ID: {}", products.size(), shopId);
        return ResponseEntity.ok(products);
    }
}
