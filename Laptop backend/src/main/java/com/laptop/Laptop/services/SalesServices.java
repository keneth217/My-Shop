package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.CartDto;
import com.laptop.Laptop.dto.Responses.CartResponse;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.enums.CartStatus;
import com.laptop.Laptop.exceptions.*;
import com.laptop.Laptop.helper.AuthUser;
import com.laptop.Laptop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalesServices {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private PdfReportServices pdfReportServices;

    // Utility method to get the logged-in user's details from the authentication token
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Ensure User implements UserDetails
    }

    @Transactional
    public CartResponse addToCart(Long productId, CartDto cartDto, User user) {
        // Ensure the provided user is the same as the logged-in user
        User loggedInUser = getLoggedInUser();

        // Find the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Check if there's enough stock
        if (product.getStock() <= 0) {
            throw new InsufficientStockException("Product " + product.getName() + " is out of stock.");
        } else if (product.getStock() < cartDto.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for " + product.getName() + ". Available stock: " + product.getStock());
        }

        // Reduce stock by quantity passed
        product.setStock(product.getStock() - cartDto.getQuantity());
        productRepository.save(product); // Save product to update stock in the database

        // Find or create a new cart for the logged-in user
        Cart cart = cartRepository.findByUser(loggedInUser)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(loggedInUser);
                    newCart.setShop(loggedInUser.getShop());
                    newCart.setStatus(CartStatus.IN_CART); // Set status to IN_CART
                    newCart.setShopCode(loggedInUser.getShop().getShopCode());
                    return newCart;
                });

        // Check if the product is already in the cart
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setProduct(product);
                    newItem.setItemCosts(product.getSellingPrice());
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
        cartItem.setQuantity(cartDto.getQuantity());

        // Update the status of the cart and cart item to IN_CART
        cart.setStatus(CartStatus.IN_CART);
        cartItem.setStatus(CartStatus.IN_CART);

        // Recalculate the total cart value
        cart.recalculateTotal();

        // Save the updated cart
        cartRepository.save(cart);

        // Create a CartResponse to return
        CartResponse response = CartResponse.builder()
                .items(cart.getItems())
                .total(cart.getTotalCart())
                .build();

        return response;
    }



    @Transactional
    public Sale checkoutCart(User user, Sale customer) throws IOException {
        User loggedInUser = getLoggedInUser();

        // Find or create the user's cart associated with the current shop
        Cart cart = cartRepository.findByUserAndShop(user, loggedInUser.getShop())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setShop(loggedInUser.getShop());
                    newCart.setStatus(CartStatus.IN_CART);
                    cartRepository.save(newCart); // Save and return the new cart
                    return newCart;
                });

        // Check if the cart has already been checked out
        if (cart.getStatus() == CartStatus.SOLD) {
            // If already checked out, create a new cart for the user
            cart = new Cart();
            cart.setUser(user);
            cart.setShop(loggedInUser.getShop());
            cart.setStatus(CartStatus.IN_CART);
            cartRepository.save(cart);
        }

        // Create and populate the Sale object
        Sale sale = new Sale();
        sale.setShopCode(loggedInUser.getShop().getShopCode());
        sale.setDate(LocalDate.now());
        sale.setSalePerson(loggedInUser.getUsername());
        sale.setUser(loggedInUser);
        sale.setCustomerAddress(customer.getCustomerAddress());
        sale.setShop(loggedInUser.getShop());
        sale.setCustomerName(customer.getCustomerName());
        sale.setCustomerPhone(customer.getCustomerPhone());

        List<SaleItem> saleItems = new ArrayList<>();
        double totalPrice = 0;

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
            saleItem.setSalePrice(product.getSellingPrice());
            saleItem.setSale(sale);
            saleItems.add(saleItem); // Accumulate sale items

            // Update total price
            totalPrice += product.getSellingPrice() * quantity;
        }

        // Save sale with its items and total price
        sale.setSaleItems(saleItems);
        sale.setTotalPrice(totalPrice);
        Sale savedSale = saleRepository.save(sale);

        // Create a receipt for the sale
        Receipt receipt = new Receipt();
        receipt.setShop(savedSale.getShop());
        receipt.setSale(savedSale);
        receipt.setReceiptDate(LocalDate.now());
        receipt.setReceiptTo(savedSale.getCustomerName());
        receipt.setUser(savedSale.getUser());
        int receiptNo = receiptRepository.findMaxReceiptNo().orElse(0) + 1;
        receipt.setReceiptNo(receiptNo);
        receiptRepository.save(receipt);

        // Generate a PDF receipt
        pdfReportServices.generateReceiptForSale(savedSale.getId());

        // Delete all cart items to clear the cart for the logged-in user
        cartItemRepository.deleteAll(cart.getItems());

        // Delete the cart itself for the logged-in user
        cartRepository.delete(cart);

        return savedSale;
    }










    // Extracted method to create a Receipt
    private Receipt createReceipt(Sale savedSale) {
        Receipt receipt = new Receipt();
        receipt.setShop(savedSale.getShop());
        receipt.setSale(savedSale);
        receipt.setReceiptDate(LocalDate.now());
        receipt.setReceiptTo(savedSale.getCustomerName());
        receipt.setUser(savedSale.getUser());

        // Ensure receipt number is unique
        int receiptNo = receiptRepository.findMaxReceiptNo().orElse(0) + 1;
        receipt.setReceiptNo(receiptNo);
        return receipt;
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

    @Transactional
    public void removeFromCart(User user, Long cartItemId) {
        User loggedInUser = getLoggedInUser();

        // Find the cart for the logged-in user in the specific shop
        Cart cart = cartRepository.findByUserAndShop(user,loggedInUser.getShop())
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user"));


        // Find the cart item by its ID
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId) && CartStatus.IN_CART.equals(item.getStatus()))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));


        // Retrieve the product associated with the cart item
        Product product = cartItem.getProduct();

        // Update the product's stock by adding back the cart item's quantity
        product.setStock(product.getStock() + cartItem.getQuantity());
        productRepository.save(product);  // Save the updated product
        // Remove the cart item from the cart
        cart.getItems().remove(cartItem);

        // Call the recalculateTotal method to update the cart total
        cart.recalculateTotal();

        // Save the updated cart
        cartRepository.save(cart);
    }

}
