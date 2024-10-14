package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.exceptions.InsufficientStockException;
import com.laptop.Laptop.exceptions.ProductNotFoundException;
import com.laptop.Laptop.helper.AuthUser;
import com.laptop.Laptop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
@Service
public class SalesServices {
    @Autowired
    private CartItemRepository  cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private PdfReportServices pdfReportServices ;


    // Utility method to get the logged-in user's details from the authentication token
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Ensure User implements UserDetails
    }
    @Transactional
    public Cart addToCart(Long productId, int quantity, User user) {
        // Ensure the provided user is the same as the logged-in user
        User loggedInUser = getLoggedInUser();

        // Find or create a new cart for the logged-in user
        Cart cart = cartRepository.findByUser(loggedInUser)
                .orElseGet(() -> {
                    Cart newCart = new Cart(loggedInUser);
                    newCart.setShop(loggedInUser.getShop());
                    newCart.setShopCode(loggedInUser.getShop().getShopCode());
                    return newCart;
                });

        // Find the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Check if the product is already in the cart
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setProduct(product);
                    newItem.setItemCosts(product.getCost());
                    newItem.setCart(cart);
                    newItem.setShop(loggedInUser.getShop());
                    newItem.setShopCode(loggedInUser.getShop().getShopCode());
                    newItem.setUser(loggedInUser);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        // Update the quantity of the cart item
        cartItem.setQuantity(cartItem.getQuantity() + quantity);

        // Recalculate the total cart value
        cart.recalculateTotal();

        // Save and return the updated cart
        return cartRepository.save(cart);
    }
    // Checkout the cart for the logged-in user, ensuring the cart belongs to their shop
    @Transactional
    public Sale checkoutCart(User user, String customerName, String customerPhone) throws IOException {
        User loggedInUser = getLoggedInUser();

        Cart cart = cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        double totalPrice = 0;
        Sale sale = new Sale();
        sale.setShopCode(loggedInUser.getShop().getShopCode());
        sale.setDate(LocalDate.now());
        sale.setSalePerson(loggedInUser.getUsername());
        sale.setUser(loggedInUser);
        sale.setShop(loggedInUser.getShop());
        sale.setCustomerName(customerName);
        sale.setCustomerPhone(customerPhone);

        Sale savedSale = saleRepository.save(sale); // Save to generate ID

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            if (product.getStock() < quantity) {
                throw new InsufficientStockException("Insufficient stock for: " + product.getName());
            }

            // Update product stock and quantity sold
            product.setStock(product.getStock() - quantity);
            product.setQuantitySold(product.getQuantitySold() + quantity);
            productRepository.save(product);

            // Create and add sale item to the sale
            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setQuantity(quantity);
            saleItem.setSalePrice(product.getPrice());
            saleItem.setSale(savedSale);
            savedSale.getSaleItems().add(saleItem);

            totalPrice += product.getPrice() * quantity;

            // Create receipt for each product sold (if needed)
            Receipt receipt = new Receipt();
            receipt.setProduct(product);  // Ensure product is assigned here
            receipt.setShop(savedSale.getShop());
            receipt.setSale(savedSale);
            receipt.setReceiptDate(LocalDate.now());
            receipt.setReceiptTo(savedSale.getCustomerName());
            receipt.setUser(savedSale.getUser());

            int receiptNo = receiptRepository.findMaxReceiptNo().orElse(0) + 1;
            receipt.setReceiptNo(receiptNo);
            receiptRepository.save(receipt);  // Save receipt for each product
        }

        // Set total price and save the sale
        savedSale.setTotalPrice(totalPrice);
        saleRepository.save(savedSale);

        // Generate PDF receipt
        pdfReportServices.generateReceiptForSale(savedSale.getId());

        // Clear the cart after checkout
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedSale;
    }
    // Fetch cart items for the logged-in user specific to their shop
    @Transactional(readOnly = true)
    public Cart getCartItems(User user) {
        User loggedInUser = getLoggedInUser();

        // Ensure the cart returned is specific to the logged-in user's shop
        return cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElse(new Cart(loggedInUser));  // If no cart exists for the user, return an empty cart
    }
}
