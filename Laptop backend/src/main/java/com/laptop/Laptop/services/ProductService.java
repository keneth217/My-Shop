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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    public List<ProductCreationRequestDto> getProductsForShop(Long shopId) {
        List<Product> products = productRepository.findByShopId(shopId);

        // Convert Product to ProductDTO
        return products.stream().map(product -> {
            ProductCreationRequestDto dto = new ProductCreationRequestDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());
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
        User user = userRepository.findByUsernameAndShopId(username, shopId)
                .orElseThrow(() -> new IllegalStateException("User not associated with this shop"));

        // Use the Builder to create a new Product instance and associate it with the shop
        Product product = Product.builder()

                .name(request.getName())  // Get product name from DTO
                .price(request.getPrice()) // Get selling price from DTO
                .cost(request.getCost())   // Get cost price from DTO
                .stock(request.getStock())
                .shopCode(shop.getShopCode())// Get stock from DTO
                .shop(shop)                // Associate the product with the shop
                .build();

        return productRepository.save(product); // Save the product to the database
    }



}
