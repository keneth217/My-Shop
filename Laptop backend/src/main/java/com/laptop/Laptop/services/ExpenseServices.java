package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.ExpenseDto; // Import ExpenseDto
import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ExpenseDto addExpense(ExpenseDto expenseDto) {
        User loggedInUser = getLoggedInUser(); // Fetch user dynamically

        // Convert DTO to Entity
        Expense expense = new Expense();
        expense.setShopCode(loggedInUser.getShopCode());
        expense.setUser(loggedInUser);
        expense.setDate(LocalDate.now());
        expense.setAmount(expenseDto.getAmount());
        expense.setType(expenseDto.getType());
        expense.setShop(loggedInUser.getShop());
        expense.setDescription(expenseDto.getDescription());

        // Save the expense to the database
        Expense savedExpense = expenseRepository.save(expense);

        // Convert saved entity back to DTO
        return mapToDto(savedExpense);
    }

    /**
     * Find expenses by type for the logged-in user's shop.
     */
    public List<ExpenseDto> findExpensesByType(ExpenseType type) {
        User loggedInUser = getLoggedInUser(); // Fetch user dynamically
        List<Expense> expenses = expenseRepository.findByShopAndType(
                loggedInUser.getShop(), type
        );

        // Convert each Expense to ExpenseDto
        return expenses.stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<ExpenseDto> getExpenseForShop() {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        List<Expense> expenses = expenseRepository.findByShop(shop);

        // Convert each Expense to ExpenseDto
        return expenses.stream()
                .map(this::mapToDto)
                .toList();
    }

    public Map<ExpenseType, Double> getExpenseTotal() {
        Map<ExpenseType, Double> expenseTotals = new HashMap<>();
        Shop shop = getUserShop(); // Assuming you have a method to get the current user's shop

        // Loop through the ExpenseType enum values
        for (ExpenseType type : ExpenseType.values()) {
            Double total = expenseRepository.sumAmountByTypeAndShop(type, shop);
            // Handle null values
            expenseTotals.put(type, (total != null) ? total : 0.0);
        }

        return expenseTotals;
    }

    // Helper method to convert Expense to ExpenseDto
    private ExpenseDto mapToDto(Expense expense) {
        return ExpenseDto.builder()
                .type(expense.getType())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .date(expense.getDate())
                .shopCode(expense.getShopCode())
                .build();
    }
}
