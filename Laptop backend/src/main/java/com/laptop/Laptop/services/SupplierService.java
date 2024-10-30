package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.StockPurchase;
import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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


    public Supplier addSupplier(Supplier supplier) {
        User loggedInUser = getLoggedInUser();
        logger.info("Adding supplier for user: {}", loggedInUser.getId());

        if (loggedInUser == null) {
            throw new IllegalArgumentException("User is not logged in.");
        }

        supplier.setShop(loggedInUser.getShop());
        supplier.setShopCode(loggedInUser.getShopCode());

        Supplier savedSupplier = supplierRepository.save(supplier);
        logger.info("Supplier added successfully: {}", savedSupplier.getId());
        return savedSupplier;
    }


    public List<Supplier> getSuppliersForShop() {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return supplierRepository.findByShop(shop);
    }
}
