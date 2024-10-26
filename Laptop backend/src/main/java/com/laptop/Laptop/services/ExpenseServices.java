package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseServices {

    @Autowired
    private ExpenseRepository expenseRepository;

    // Utility method to get the logged-in user's details from the authentication token
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Ensure User implements UserDetails
    }
    private Shop getUserShop() {
        return getLoggedInUser().getShop();
    }

    /**
     * Add a new expense for the logged-in user.
     */
    public Expense addExpense(Expense expense) {
        User loggedInUser = getLoggedInUser(); // Fetch user dynamically

        // Set expense-specific details
        expense.setShopCode(loggedInUser.getShopCode());
        expense.setUser(loggedInUser);
        expense.setDate(LocalDate.now());
        expense.setShop(loggedInUser.getShop());

        // Save the expense to the database
        return expenseRepository.save(expense);
    }

    /**
     * Find expenses by type for the logged-in user's shop.
     */
    public List<Expense> findExpensesByType(ExpenseType expenseType) {
        User loggedInUser = getLoggedInUser(); // Fetch user dynamically
        return expenseRepository.findByShopAndExpenseType(
                loggedInUser.getShop(), expenseType.name()
        );
    }

    public List<Expense> getExpenseForShop() {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        return expenseRepository.findByShop(shop);
    }
}
