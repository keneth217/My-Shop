package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.StockPurchase;
import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.repository.SupplierRepository;
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
        // Set shop and shop code for the supplier
        supplier.setShop(loggedInUser.getShop());
        supplier.setShopCode(loggedInUser.getShopCode());

        return supplierRepository.save(supplier);
    }


    public List<Supplier> getSuppliersForShop() {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return supplierRepository.findByShop(shop);
    }
}
