package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.exceptions.InsufficientStockException;
import com.laptop.Laptop.exceptions.ProductNotFoundException;
import com.laptop.Laptop.helper.AuthUser;
import com.laptop.Laptop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
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
    private AuthUser authUser;

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private PdfReportServices pdfReportServices ;
    @Transactional
    public Cart addToCart(Long productId, int quantity, User user) {

        // Get the logged-in user (assumed to be the same as the provided user)
        User loggedInUser = authUser.getLoggedInUserDetails();

        // Find the cart for the logged-in user or create a new one
        Cart cart = cartRepository.findByUser(loggedInUser)
                .orElse(new Cart(loggedInUser)); // Create a new cart for the logged-in user if not found

        // Find the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Check if the product is already in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // If the product already exists in the cart, update the quantity
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            // If the product is not in the cart, create a new CartItem
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);

            // Set shop details from the logged-in user
            cartItem.setShop(loggedInUser.getShop()); // Set the shop
            cartItem.setShopCode(loggedInUser.getShop().getShopCode()); // Set the shop code

            // Set the logged-in user for the CartItem
            cartItem.setUser(loggedInUser);

            // Add the new CartItem to the cart
            cart.getItems().add(cartItem);
        }

        // Save and return the updated cart
        return cartRepository.save(cart);
    }
    // Checkout the cart for the logged-in user, ensuring the cart belongs to their shop
    @Transactional
    public Sale checkoutCart(User user, String customerName, String customerPhone) throws java.io.IOException {
        User loggedInUser = authUser.getLoggedInUserDetails();

        // Fetch cart for the logged-in user's shop
        Cart cart = cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for the logged-in user's shop"));

        double totalPrice = 0;
        Sale sale = new Sale(); // Create new sale object

        // Set sale details before saving
        sale.setShopCode(loggedInUser.getShop().getShopCode());
        sale.setDate(LocalDate.now());
        sale.setSalePerson(loggedInUser.getUsername());
        sale.setUser(loggedInUser);  // Associate with the logged-in user
        sale.setShop(loggedInUser.getShop());
        sale.setCustomerName(customerName);
        sale.setCustomerPhone(customerPhone);

        // Save sale before adding sale items
        Sale savedSale = saleRepository.save(sale);

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            if (product.getStock() < quantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            // Deduct stock and update product
            product.setStock(product.getStock() - quantity);
            product.setQuantitySold(product.getQuantitySold() + quantity);
            productRepository.save(product);

            // Create SaleItem for each product
            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setQuantity(quantity);
            saleItem.setSalePrice(product.getPrice());
            saleItem.setSale(savedSale);  // Associate SaleItem with the saved sale

            savedSale.getSaleItems().add(saleItem);  // Add SaleItem to Sale

            // Add to total price
            totalPrice += product.getPrice() * quantity;
        }

        // Update total price after adding items
        savedSale.setTotalPrice(totalPrice);
        saleRepository.save(savedSale);  // Save sale again with updated total price

        // Generate and save receipt for later reference
        ByteArrayInputStream receiptStream = pdfReportServices.generateReceiptForSale(savedSale.getId());

        // Optionally, send the receipt via email or another method

        // Clear the cart after checkout
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedSale;
    }
    // Fetch cart items for the logged-in user specific to their shop
    @Transactional(readOnly = true)
    public Cart getCartItems(User user) {
        User loggedInUser = authUser.getLoggedInUserDetails();

        // Ensure the cart returned is specific to the logged-in user's shop
        return cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElse(new Cart(loggedInUser));  // If no cart exists for the user, return an empty cart
    }
}
