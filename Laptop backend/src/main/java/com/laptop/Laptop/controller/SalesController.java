package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.CartDto;
import com.laptop.Laptop.dto.Responses.CartResponse;
import com.laptop.Laptop.entity.Cart;
import com.laptop.Laptop.entity.CartItem;
import com.laptop.Laptop.entity.Sale;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.exceptions.ProductNotFoundException;
import com.laptop.Laptop.repository.SaleRepository;
import com.laptop.Laptop.services.PdfReportServices;
import com.laptop.Laptop.services.SalesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {
    @Autowired
    private SalesServices salesServices;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private PdfReportServices pdfReportServices;

    // Utility method to get the logged-in user's details from the authentication token
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Ensure User implements UserDetails
    }
    @PostMapping("/add/{productId}")
    public ResponseEntity<CartResponse> addToCart(@PathVariable Long productId, @RequestBody CartDto quantity) {
        User loggedInUser = getLoggedInUser();
        // Implement this to get the logged-in user
        CartResponse cart = salesServices.addToCart(productId, quantity, loggedInUser);
        return ResponseEntity.ok(cart);
    }



    @PostMapping("/checkout")
    public ResponseEntity<Sale> checkoutCart( @RequestBody Sale customer) throws IOException {
        User user = getLoggedInUser(); // Assuming this retrieves the currently logged-in user
        Sale savedSale = salesServices.checkoutCart( user, customer);
        return ResponseEntity.ok(savedSale);
    }

    // Get the list of cart items and the total price for the logged-in user
    @GetMapping("/items")
    public ResponseEntity<CartResponse> getCartItems() {
        User loggedInUser = getLoggedInUser();
        CartResponse cartResponse = salesServices.getCartItems(loggedInUser);
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        User loggedInUser = getLoggedInUser();

        salesServices.removeFromCart(loggedInUser,cartItemId);

        return ResponseEntity.noContent().build(); // Return 204 No Content
    }


    // Get list of sales for the logged-in user and shop
    @GetMapping
    public ResponseEntity<List<Sale>> getSalesForShop() {
        User loggedInUser = getLoggedInUser();
        List<Sale> sales = salesServices.getSalesForShop(loggedInUser);
        return ResponseEntity.ok(sales);
    }


    /**
     * Generate and download the receipt in PDF format for a given Sale ID.
     *
     * @param saleId The ID of the sale for which the receipt will be generated.
     * @return ResponseEntity containing the receipt PDF as a stream.
     */
    @GetMapping("/generate/{saleId}")
    public ResponseEntity<byte[]> generateReceipt(@PathVariable Long saleId) {
        try {
            // Generate PDF receipt from sale ID
            ByteArrayInputStream bis = pdfReportServices.generateReceiptForSale(saleId);

            // Set HTTP headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=receipt_" + saleId + ".pdf");

            // Return the PDF as a byte array in the response
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(bis.readAllBytes());
        } catch (IOException e) {
            // Handle IOException if PDF generation fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating receipt: " + e.getMessage()).getBytes());
        } catch (ProductNotFoundException e) {
            // Handle case where the sale is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Sale not found: " + e.getMessage()).getBytes());
        }
    }
}
