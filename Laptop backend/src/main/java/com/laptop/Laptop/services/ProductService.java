package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.ProductCreationRequestDto;
import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.repository.ProductRepository;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable("products")
    public List<ProductCreationRequestDto> getProductsForShop(Long shopId) {
        List<Product> products = productRepository.findByShopId(shopId);

        return products.stream().map(product -> {
            ProductCreationRequestDto dto = new ProductCreationRequestDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setProductFeatures(product.getProductFeatures());
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());

            List<String> productImagesAsBase64 = product.getProductImages().stream()
                    .map(image -> Base64.getEncoder().encodeToString(image))
                    .collect(Collectors.toList());
            dto.setProductImagesList(productImagesAsBase64);

            return dto;
        }).collect(Collectors.toList());
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product addProductToShop(Long shopId, ProductCreationRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalStateException("Shop not found"));

        userRepository.findByUsernameAndShopId(username, shopId)
                .orElseThrow(() -> new IllegalStateException("User not associated with this shop"));

        List<byte[]> productImages = new ArrayList<>();
        if (request.getProductImages() != null) {
            for (MultipartFile file : request.getProductImages()) {
                try {
                    productImages.add(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Error processing uploaded image files", e);
                }
            }
        }

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .cost(request.getCost())
                .stock(request.getStock())
                .productFeatures(request.getProductFeatures())
                .productImages(productImages)
                .shopCode(shop.getShopCode())
                .shop(shop)
                .build();

        return productRepository.save(product);
    }

    @Cacheable(value = "products", key = "#productId")
    public ProductCreationRequestDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        ProductCreationRequestDto dto = new ProductCreationRequestDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setProductFeatures(product.getProductFeatures());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());

        List<String> productImagesAsBase64 = product.getProductImages().stream()
                .map(image -> Base64.getEncoder().encodeToString(image))
                .collect(Collectors.toList());
        dto.setProductImagesList(productImagesAsBase64);

        return dto;
    }

    @CacheEvict(value = "products", key = "#productId")
    public void updateProduct(Long productId, ProductCreationRequestDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        // Update product fields from the request DTO
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setProductFeatures(request.getProductFeatures());

        // Update product images if provided
        if (request.getProductImages() != null) {
            List<byte[]> updatedImages = request.getProductImages().stream()
                    .map(file -> {
                        try {
                            return file.getBytes();
                        } catch (IOException e) {
                            throw new RuntimeException("Error processing image", e);
                        }
                    }).collect(Collectors.toList());
            product.setProductImages(updatedImages);
        }

        productRepository.save(product);
    }

    @CacheEvict(value = "products", key = "#productId")
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalStateException("Product not found");
        }

        productRepository.deleteById(productId);
    }
}
