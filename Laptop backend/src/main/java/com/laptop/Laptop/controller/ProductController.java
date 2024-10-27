package com.laptop.Laptop.controller;

import com.laptop.Laptop.constants.ProductConstants;
import com.laptop.Laptop.dto.ProductCreationRequestDto;
import com.laptop.Laptop.dto.Responses.Responsedto;
import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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

    // Add Product Endpoint
    @PostMapping("/add")
    public ResponseEntity<Responsedto> addProduct(@ModelAttribute ProductCreationRequestDto request, HttpServletRequest httpRequest) {
        Long shopId = (Long) httpRequest.getAttribute("shopId");
        Product newProduct = productService.addProductToShop(shopId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(
                        ProductConstants.PRODUCT_ADDITION,
                        ProductConstants.PRODUCT_ADDITION_CODE
                ));
    }

    // Retrieve All Products for a Shop
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

    // Retrieve Single Product by ID
    @GetMapping("/{productId}")
    public ResponseEntity<ProductCreationRequestDto> getProductById(@PathVariable Long productId) {
        ProductCreationRequestDto product = productService.getProductById(productId);

        if (product == null) {
            logger.warn("Product with ID {} not found", productId);
            return ResponseEntity.notFound().build();
        }

        logger.info("Retrieved product with ID: {}", productId);
        return ResponseEntity.ok(product);
    }

    // Update Product by ID
    @PutMapping("/update/{productId}")
    public ResponseEntity<Responsedto> updateProduct(
            @PathVariable Long productId,
            @ModelAttribute ProductCreationRequestDto request) {
        try {
            productService.updateProduct(productId, request);

            logger.info("Product with ID {} updated successfully", productId);
            return ResponseEntity.ok(new Responsedto(
                    ProductConstants.PRODUCT_UPDATE,
                    ProductConstants.PRODUCT_UPDATE_CODE
            ));
        } catch (IllegalStateException e) {
            logger.error("Failed to update product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Responsedto(
                    "Product not found",
                    "PRODUCT_NOT_FOUND"
            ));
        }
    }

    // Delete Product by ID
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Responsedto> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);

            logger.info("Product with ID {} deleted successfully", productId);
            return ResponseEntity.ok(new Responsedto(
                    ProductConstants.PRODUCT_DELETION,
                    ProductConstants.PRODUCT_DELETION_CODE
            ));
        } catch (IllegalStateException e) {
            logger.error("Failed to delete product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Responsedto(
                    "Product not found",
                    "PRODUCT_NOT_FOUND"
            ));
        }
    }
}
