package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.ShopUpdateRequestDto;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.exceptions.ShopNotFoundException;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class CustomerShopService {

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private  UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


    @Transactional
    public ShopUpdateRequestDto updateShopDetailsForCurrentUser(ShopUpdateRequestDto request) {
        // Get the logged-in user's username from the security context
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUsername));

        // Ensure the user has an associated shop
        Shop shop = user.getShop();
        if (shop == null) {
            throw new ShopNotFoundException("No shop associated with the current user");
        }

        // Validate the request
        validateShopUpdateRequest(request);

        // Update shop details based on the request
        shop.setShopName(request.getShopName());
        shop.setAddress(request.getAddress());
        shop.setOwner(request.getOwner());
        shop.setPhoneNumber(request.getPhoneNumber());
        shop.setDescription(request.getDescription());

        // Update shop logo if provided
        if (request.getShopLogo() != null) {
            try {
                // Get the logo bytes from the uploaded file
                byte[] logoBytes = request.getShopLogo().getBytes(); // Assuming shopLogo is a MultipartFile in the request
                shop.setShopLogo(logoBytes); // Set the byte array in the shop entity

                // Optionally handle the URL or other logic here if needed
                // For example, if you have a method to generate a URL for the logo:
                // String logoUrl = generateLogoUrl(shop.getId());
                // shop.setShopLogoUrl(logoUrl);

            } catch (IOException e) {
                logger.error("Error while uploading shop logo", e);
                throw new RuntimeException("Failed to store shop logo", e);
            }
        }

        // Save updated shop details
        shopRepository.save(shop);

        // Log the updated shop details
        logger.info("Updated shop details: {}", shop);

        // Return updated details as a DTO
        return convertShopToShopUpdateRequestDto(shop);
    }

    // Method to convert Shop entity to ShopUpdateRequestDto
    private ShopUpdateRequestDto convertShopToShopUpdateRequestDto(Shop shop) {
        String logoUrl = null;
        if (shop.getShopLogo() != null) {
            // Convert byte array to base64 or handle accordingly if you store it as a URL
            logoUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(shop.getShopLogo());
        }

        return ShopUpdateRequestDto.builder()
                .shopName(shop.getShopName())
                .address(shop.getAddress())
                .owner(shop.getOwner())
                .shopCode(shop.getShopCode())
                .phoneNumber(shop.getPhoneNumber())
                .description(shop.getDescription())
                .shopLogoUrl(logoUrl) // Return base64 encoded logo
                .build();
    }


    // Example validation method
    private void validateShopUpdateRequest(ShopUpdateRequestDto request) {
        if (request.getShopName() == null || request.getShopName().isEmpty()) {
            throw new IllegalArgumentException("Shop name must not be empty");
        }
        // Add other validation checks as needed, e.g., validate phone number format, address, etc.
    }


    public ShopUpdateRequestDto getShopDetailsForCurrentUser() {
        // Get the currently authenticated user's username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Retrieve the user by username
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUsername));

        // Ensure the user has an associated shop
        Shop shop = user.getShop();
        if (shop == null) {
            throw new ShopNotFoundException("No shop associated with the current user");
        }


        String logoUrl = null;
        if (shop.getShopLogo() != null) {
            // Convert byte array to base64 or handle accordingly if you store it as a URL
            logoUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(shop.getShopLogo());
        }
        System.out.println();

        // Return shop details, including the logo URL
        return ShopUpdateRequestDto.builder()
                .shopName(shop.getShopName())
                .address(shop.getAddress())
                .owner(shop.getOwner())
                .shopCode(shop.getShopCode())
                .phoneNumber(shop.getPhoneNumber())
                .description(shop.getDescription())
                .shopLogoUrl(logoUrl) // Retrieve and return the logo URL
                .build();

    }


}
