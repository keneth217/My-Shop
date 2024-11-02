package com.laptop.Laptop.services;

import com.laptop.Laptop.controller.ProductController;
import com.laptop.Laptop.dto.ProductCreationRequestDto;
import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.exceptions.ImageProcessingException;
import com.laptop.Laptop.exceptions.ProductNotFoundException;
import com.laptop.Laptop.repository.ProductRepository;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    //@Cacheable(value = "productsByShop", key = "#shopId")
    public List<ProductCreationRequestDto> getProductsForShop(Long shopId) {
        logger.info("Fetching products for shopId: {}", shopId);
        List<Product> products = productRepository.findByShopId(shopId);

        return products.stream()
                .filter(product -> product.getStock() > 0) // Filter products with stock greater than 0
                .map(product -> {
                    ProductCreationRequestDto dto = new ProductCreationRequestDto();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setProductFeatures(product.getProductFeatures());
                    dto.setStock(product.getStock());

                    List<String> productImagesAsBase64 = product.getProductImages().stream()
                            .map(image -> Base64.getEncoder().encodeToString(image))
                            .collect(Collectors.toList());
                    dto.setProductImagesList(productImagesAsBase64);

                    return dto;
                })
                .collect(Collectors.toList());
    }


    //@Cacheable(value = "productsByShopStock", key = "#shopId")
    public List<ProductCreationRequestDto> getProductsForShopToStock(Long shopId) {
        logger.info("Fetching products for shopId: {}", shopId);
        List<Product> products = productRepository.findByShopId(shopId);

        return products.stream()
                .filter(product -> product.getStock()  < 1) // Filter products with stock equal to 0
                .map(product -> {
                    ProductCreationRequestDto dto = new ProductCreationRequestDto();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setProductFeatures(product.getProductFeatures());
                    dto.setStock(product.getStock());

                    List<String> productImagesAsBase64 = product.getProductImages().stream()
                            .map(image -> Base64.getEncoder().encodeToString(image))
                            .collect(Collectors.toList());
                    dto.setProductImagesList(productImagesAsBase64);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    //@CacheEvict(value = {"products", "productsByShop"}, allEntries = true)
    public Product addProductToShop(Long shopId, ProductCreationRequestDto request) throws ImageProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalStateException("Shop not found"));

        userRepository.findByUsernameAndShopId(username, shopId)
                .orElseThrow(() -> new IllegalStateException("User not associated with this shop"));

        // Validate the product request
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }

        List<byte[]> productImages = new ArrayList<>();
        if (request.getProductImages() != null) {
            for (MultipartFile file : request.getProductImages()) {
                try {
                    productImages.add(file.getBytes());
                } catch (IOException e) {
                    throw new ImageProcessingException("Error processing uploaded image files", e);
                }
            }
        }

        Product product = Product.builder()
                .name(request.getName())
                .sellingPrice(request.getSellingPrice())
                .cost(request.getBuyingPrice())
                .stock(request.getStock())
                .productFeatures(request.getProductFeatures())
                .productImages(productImages)
                .shopCode(shop.getShopCode())
                .shop(shop)
                .build();

        logger.info("User {} is adding a new product: {}", username, product);

        return productRepository.save(product);
    }

   // @Cacheable(value = "products", key = "'product:' + #productId")
    public ProductCreationRequestDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        logger.info("Retrieved product: {}", product);

        ProductCreationRequestDto dto = new ProductCreationRequestDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setProductFeatures(product.getProductFeatures());
        dto.setBuyingPrice(product.getCost());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setStock(product.getStock());

        List<String> productImagesAsBase64 = product.getProductImages().stream()
                .map(image -> Base64.getEncoder().encodeToString(image))
                .collect(Collectors.toList());
        dto.setProductImagesList(productImagesAsBase64);

        return dto;
    }

   // @CacheEvict(value = {"products", "productsByShop"}, allEntries = true)
    public void updateProduct(Long productId, ProductCreationRequestDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        // Update product fields from the request DTO
        product.setName(request.getName());
        product.setCost(request.getBuyingPrice());
        product.setSellingPrice(request.getSellingPrice());
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

   // @CacheEvict(value = {"products", "productsByShop"}, allEntries = true)
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found");
        }

        productRepository.deleteById(productId);
    }
}
