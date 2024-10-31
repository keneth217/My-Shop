package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Investment;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.helper.AuthUser;
import com.laptop.Laptop.repository.InvestmentRepository;
import com.laptop.Laptop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvestmentService {
    @Autowired
    private InvestmentRepository investmentRepository;

    // Utility method to get the logged-in user's details from the authentication token
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Ensure User implements UserDetails
    }
    private Shop getUserShop() {
        return getLoggedInUser().getShop();
    }
    // Handle investment
    public Investment addInvestment(Investment investment) {
        User loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            throw new IllegalStateException("User must be logged in to add an investment.");
        }

        if (investment.getAmount() <= 0) {
            throw new IllegalArgumentException("Investment amount must be positive.");
        }

        investment.setDate(LocalDate.now());
        investment.setShopCode(loggedInUser.getShopCode());
        investment.setShop(loggedInUser.getShop());
        investment.setUser(loggedInUser);
        investment.setDescription("Initial investment");

        try {
            return investmentRepository.save(investment);
        } catch (Exception e) {
            throw new RuntimeException("Error saving investment: " + e.getMessage(), e);
        }
    }

    public List<Investment> getInvestementForShop() {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return investmentRepository.findByShop(shop);
    }
}
