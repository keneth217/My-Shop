package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.ProductCreationRequestDto;
import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.repository.ProductRepository;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;


    public List<ProductCreationRequestDto> getProductsForShop(Long shopId) {
        List<Product> products = productRepository.findByShopId(shopId);

        // Convert Product to ProductDTO
        return products.stream().map(product -> {
            ProductCreationRequestDto dto = new ProductCreationRequestDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setProductFeatures(product.getProductFeatures());
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());

            // Convert byte[] to Base64 String for each image
            List<String> productImagesAsBase64 = product.getProductImages().stream()
                    .map(image -> Base64.getEncoder().encodeToString(image))
                    .collect(Collectors.toList());
            dto.setProductImagesList(productImagesAsBase64);

            return dto;
        }).collect(Collectors.toList());
    }


    public Product addProductToShop(Long shopId, ProductCreationRequestDto request) {
        // Fetch the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the username of the authenticated user

        // Fetch the shop based on shopId
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalStateException("Shop not found"));

        // Check if the user is associated with the shop
        userRepository.findByUsernameAndShopId(username, shopId)
                .orElseThrow(() -> new IllegalStateException("User not associated with this shop"));

        // Convert MultipartFile[] to List<byte[]>
        List<byte[]> productImages = new ArrayList<>();
        if (request.getProductImages() != null) {
            for (MultipartFile file : request.getProductImages()) {
                try {
                    productImages.add(file.getBytes());  // Convert each file to byte array
                } catch (IOException e) {
                    throw new RuntimeException("Error processing uploaded image files", e);
                }
            }
        }

        // Build the product
        Product product = Product.builder()
                .name(request.getName())  // Set product name from DTO
                .price(0) // Set selling price from DTO
                .cost(0)   // Set cost price from DTO
                .stock(0) // Set stock from DTO
                .productFeatures(request.getProductFeatures()) // Set features from DTO
                .productImages(productImages)  // Set converted image byte arrays
                .shopCode(shop.getShopCode())  // Associate product with the shop code
                .shop(shop)                    // Associate product with the shop entity
                .build();

        // Save the product to the database
        return productRepository.save(product);


    }
}
