package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.Investment;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.helper.AuthUser;
import com.laptop.Laptop.repository.InvestmentRepository;
import com.laptop.Laptop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InvestmentService {
    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private AuthUser authUser;
    // Handle investment
    public Investment addInvestment(Investment investment) {
        User loggedInUser = authUser.getLoggedInUserDetails();
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
}
