package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.InvestmentDto;
import com.laptop.Laptop.entity.Investment;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.repository.InvestmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    public InvestmentDto addInvestment(InvestmentDto investmentDto) {
        User loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            throw new IllegalStateException("User must be logged in to add an investment.");
        }

        if (investmentDto.getAmount() <= 0) {
            throw new IllegalArgumentException("Investment amount must be positive.");
        }

        // Create and populate the Investment entity
        Investment investment = Investment.builder()
                .amount(investmentDto.getAmount())
                .date(LocalDate.now())
                .shopCode(loggedInUser.getShopCode())
                .shop(loggedInUser.getShop())
                .user(loggedInUser)
                .description("Initial investment")
                .build();

        try {
            Investment savedInvestment = investmentRepository.save(investment);
            return convertToDto(savedInvestment); // Convert to InvestmentDto before returning
        } catch (Exception e) {
            throw new RuntimeException("Error saving investment: " + e.getMessage(), e);
        }
    }

    public List<InvestmentDto> getInvestmentForShop() {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        List<Investment> investments = investmentRepository.findByShop(shop);
        return investments.stream()
                .map(this::convertToDto) // Convert each Investment to InvestmentDto
                .collect(Collectors.toList());
    }

    // Helper method to convert Investment to InvestmentDto
    private InvestmentDto convertToDto(Investment investment) {
        return InvestmentDto.builder()
                .amount(investment.getAmount())
                .description(investment.getDescription())
                .shopCode(investment.getShopCode())
                .date(investment.getDate())
                .build();
    }
}
