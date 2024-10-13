package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.DashboardDTO;
import com.laptop.Laptop.dto.ProductCreationRequestDto;
import com.laptop.Laptop.entity.Cart;
import com.laptop.Laptop.entity.Sale;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.services.BusinessService;
import com.laptop.Laptop.services.DashboardService;
import com.laptop.Laptop.services.PdfReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private PdfReportServices pdfReportServices ;
    // Utility method to get the logged-in user's details (shopId, shopCode, username) from token
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Assuming your `User` implements `UserDetails`
    }

    // Method to get specific details of the logged-in user
    public User getLoggedInUserDetails() {
        return getLoggedInUser(); // Directly return the logged-in user
    }
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(Pageable pageable) {
        DashboardDTO dashboardData = dashboardService.getDashboardData(pageable);
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/sales/{saleId}/generate-receipt")
    public ResponseEntity<?> generateReceipt(@PathVariable Long saleId) throws IOException {
        ByteArrayInputStream bis = pdfReportServices.generateReceiptForSale(saleId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=receipt_" + saleId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }





}
