package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.Responses.CartResponse;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.enums.CartStatus;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Cart addToCart(Long productId, Cart quantity, User user) {
        // Ensure the provided user is the same as the logged-in user
        User loggedInUser = getLoggedInUser();

        // Find the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Find or create a new cart for the logged-in user
        Cart cart = cartRepository.findByUser(loggedInUser)
                .orElseGet(() -> {
                    Cart newCart = new Cart(loggedInUser);
                    newCart.setShop(loggedInUser.getShop());
                    newCart.setQuantity(quantity.getQuantity() +quantity.getQuantity());
                    newCart.setTotalCart( quantity.getTotalCart() + product.getCost() * quantity.getQuantity());
                    newCart.setStatus(CartStatus.IN_CART); // Set status to IN_CART
                    newCart.setShopCode(loggedInUser.getShop().getShopCode());
                    return newCart;
                });

        // Check if there's enough stock
        if (product.getStock() < quantity.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for: " + product.getName());
        }
        //reduce stock  by quantity passed
        product.setStock(product.getStock()- quantity.getQuantity());

        // Check if the product is already in the cart
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setProduct(product);
                    newItem.setItemCosts(product.getCost());
                    newItem.setCart(cart);
                    newItem.setProductName(product.getName());
                    newItem.setStatus(CartStatus.IN_CART); // Set status to IN_CART
                    newItem.setShop(loggedInUser.getShop());
                    newItem.setShopCode(loggedInUser.getShop().getShopCode());
                    newItem.setUser(loggedInUser);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        // Update the quantity of the cart item
        cartItem.setQuantity(cartItem.getQuantity() + quantity.getQuantity());

        // Update the status of the cart and cart item to IN_CART
        cart.setStatus(CartStatus.IN_CART);
        cartItem.setStatus(CartStatus.IN_CART);

        // Recalculate the total cart value
        cart.recalculateTotal();

        // Save and return the updated cart
        return cartRepository.save(cart);
    }

    @Transactional
    public Sale checkoutCart(User user, Sale customer) throws IOException {
        User loggedInUser = getLoggedInUser();

        // Find the user's cart associated with the current shop
        Cart cart = cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        double totalPrice = 0;

        // Create the Sale object
        Sale sale = new Sale();
        sale.setShopCode(loggedInUser.getShop().getShopCode());
        sale.setDate(LocalDate.now());
        sale.setSalePerson(loggedInUser.getUsername());
        sale.setUser(loggedInUser);
        sale.setShop(loggedInUser.getShop());
        sale.setCustomerName(customer.getCustomerName());
        sale.setCustomerPhone(customer.getCustomerPhone());

        // Save the sale to generate an ID
        Sale savedSale = saleRepository.save(sale);

        // Process each cart item
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            // Update product stock and quantity sold

            product.setQuantitySold(product.getQuantitySold() + quantity);
            productRepository.save(product);

            // Create and associate a SaleItem with the Sale
            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setQuantity(quantity);
            saleItem.setSalePrice(product.getPrice());
            saleItem.setSale(savedSale);
            savedSale.getSaleItems().add(saleItem);

            totalPrice += product.getPrice() * quantity;
        }

        // Set the total price and save the Sale
        savedSale.setTotalPrice(totalPrice);
        saleRepository.save(savedSale);

        // Create a Receipt for the entire sale
        Receipt receipt = new Receipt();
        receipt.setShop(savedSale.getShop());
        receipt.setSale(savedSale);
        receipt.setReceiptDate(LocalDate.now());
        receipt.setReceiptTo(savedSale.getCustomerName());
        receipt.setUser(savedSale.getUser());

        // Generate a new receipt number
        int receiptNo = receiptRepository.findMaxReceiptNo().orElse(0) + 1;
        receipt.setReceiptNo(receiptNo);

        // Save the Receipt
        receiptRepository.save(receipt);

        // Generate the PDF receipt
        pdfReportServices.generateReceiptForSale(savedSale.getId());

        // Update the cart status to SOLD
        cart.setStatus(CartStatus.SOLD);
        cartRepository.save(cart); // Save the cart with updated status

        // Update each cart item's status to SOLD
        for (CartItem cartItem : cart.getItems()) {
            cartItem.setStatus(CartStatus.SOLD); // Update each cart item status to SOLD
        }
        cartItemRepository.saveAll(cart.getItems()); // Save all updated cart items

        return savedSale;
    }
    @Transactional(readOnly = true)
    public List<Sale> getSalesForShop(User user) {
        User loggedInUser = getLoggedInUser();

        // Retrieve sales based on user and shop
        return saleRepository.findByUserAndShop(loggedInUser, loggedInUser.getShop());
    }

    @Transactional(readOnly = true)
    public CartResponse getCartItems(User user) {
        User loggedInUser = getLoggedInUser();

        // Find the cart for the logged-in user in the specific shop
        Cart cart = cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElse(new Cart(loggedInUser));  // Return an empty cart if none exists

        // Filter items that are in the cart and calculate the total price
        List<CartItem> itemsInCart = cart.getItems().stream()
                .filter(item -> CartStatus.IN_CART.equals(item.getStatus())) // Filter items whose status is IN_CART
                .collect(Collectors.toList());

        double total = itemsInCart.stream()
                .mapToDouble(item -> item.getItemCosts() * item.getQuantity()) // assuming itemCosts is the price per item
                .sum();

        // Return the list of cart items and the total price
        return new CartResponse(itemsInCart, total);
    }


    @Transactional(readOnly = true)
    public int getCartItemCount(User user) {
        User loggedInUser = getLoggedInUser();

        // Find the cart for the logged-in user in the specific shop
        Cart cart = cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElse(new Cart(loggedInUser));  // Return an empty cart if none exists
        // Return the count of items in the cart whose status is IN_CART
        return (int) cart.getItems().stream()
                .filter(item -> CartStatus.IN_CART.equals(item.getStatus())) // Filter items whose status is IN_CART
                .count();
    }

}
