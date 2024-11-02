package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.StockPurchaseDto;
import com.laptop.Laptop.dto.SupplierDto; // Import SupplierDto
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.StockPurchase;
import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
// Other imports remain the same

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    private static final Logger logger = LoggerFactory.getLogger(SupplierService.class);

    // Utility method to get the logged-in user's details (shopId, shopCode, username) from token
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Assuming your `User` implements `UserDetails`
    }

    // Method to get specific details of the logged-in user
    public User getLoggedInUserDetails() {
        return getLoggedInUser(); // Directly return the logged-in user
    }

    private Shop getUserShop() {
        return getLoggedInUser().getShop();
    }

    public SupplierDto addSupplier(SupplierDto supplierDto) {
        User loggedInUser = getLoggedInUser();

        if (loggedInUser == null) {
            throw new IllegalArgumentException("User is not logged in.");
        }

        if (loggedInUser.getShop() == null || loggedInUser.getShopCode() == null) {
            throw new IllegalArgumentException("User does not have a valid shop or shop code.");
        }

        Supplier supplier = mapToEntity(supplierDto); // Convert DTO to Entity
        supplier.setShop(loggedInUser.getShop());
        supplier.setShopCode(loggedInUser.getShopCode());

        try {
            Supplier savedSupplier = supplierRepository.save(supplier);
            logger.info("Supplier added successfully: {}", savedSupplier.getId());
            return mapToDto(savedSupplier); // Convert saved entity back to DTO
        } catch (DataIntegrityViolationException e) {
            logger.error("Error saving supplier: {}", e.getMessage());
            throw new RuntimeException("Error saving supplier: " + e.getMessage());
        }
    }

    public List<SupplierDto> getSuppliersForShop() {
        Shop shop = getUserShop();
        List<Supplier> suppliers = supplierRepository.findByShop(shop);
        return suppliers.stream()
                .map(this::mapToDto)
                .toList();
    }

    // Helper method to map SupplierDto to Supplier
    // Convert SupplierDto to Supplier (entity)
    private Supplier mapToEntity(SupplierDto supplierDto) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierDto.getSupplierName());
        supplier.setSupplierAddress(supplierDto.getSupplierAddress());
        supplier.setSupplierPhone(supplierDto.getSupplierPhone());
        supplier.setSupplierLocation(supplierDto.getSupplierLocation());
        // We typically don't set stockPurchases when creating a supplier
        return supplier;
    }

    // Convert Supplier (entity) to SupplierDto
    private SupplierDto mapToDto(Supplier supplier) {
        SupplierDto supplierDto = SupplierDto.builder()
                .id(supplier.getId())
                .supplierName(supplier.getSupplierName())
                .supplierAddress(supplier.getSupplierAddress())
                .supplierPhone(supplier.getSupplierPhone())
                .supplierLocation(supplier.getSupplierLocation())
                .shopCode(supplier.getShopCode())
                .build();

        // Map stockPurchases to StockPurchaseDto
        if (supplier.getStockPurchases() != null) {
            List<StockPurchaseDto> purchaseDtos = supplier.getStockPurchases().stream()
                    .map(this::mapToStockPurchaseDto)
                    .toList();
            supplierDto.setStockPurchases(purchaseDtos);
        }

        return supplierDto;
    }

    // Convert StockPurchase to StockPurchaseDto
    private StockPurchaseDto mapToStockPurchaseDto(StockPurchase purchase) {
        return StockPurchaseDto.builder()
                .id(purchase.getId())
                .purchaseDate(purchase.getPurchaseDate())
                .quantity(purchase.getQuantity())
                .totalCost(purchase.getTotalCost())
                .build();
    }
}
