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
        System.out.println(loggedInUser);
        System.out.println("--------------addding investement------------");
        investment.setDate(LocalDate.now());
        investment.setShopCode(loggedInUser.getShopCode());
        investment.setShop(loggedInUser.getShop());
        investment.setUser(loggedInUser);
        investment.setDescription("Initial investement");
        investment.setAmount(investment.getAmount());
        return investmentRepository.save(investment);
    }

    public List<Investment> getInvestementForShop() {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return investmentRepository.findByShop(shop);
    }
}
