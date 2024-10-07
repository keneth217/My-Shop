package com.laptop.Laptop.controller;

import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.services.BusinessService;
import com.laptop.Laptop.services.PdfReportServices;
import com.laptop.Laptop.services.ShopService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pdf/reports")
public class PdfReportController {

    @Autowired
    private PdfReportServices pdfReportServices;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopRepository shopRepository;


    // Generate product stock report PDF
    @GetMapping("/products")
    public ResponseEntity<?> generateProductStockReport(@AuthenticationPrincipal User loggedInUser) throws IOException {
        // Fetch the shop details using the shopId from the logged-in user's token
        Long shopId = loggedInUser.getShop().getId(); // Extract the shopId from the logged-in user's token
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));  // Ensure shop exists

        // Generate the PDF report for the logged-in user's shop products
        ByteArrayInputStream pdfStream = pdfReportServices.generateProductsReport(shop, loggedInUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=product-stock-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }

    // Generate sales report PDF
    @GetMapping("/sales")
    public ResponseEntity<?> generateSalesReport(

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User loggedInUser) throws IOException {
// Fetch the shop details using the shopId from the logged-in user's token
        Long shopId = loggedInUser.getShop().getId(); // Extract the shopId from the logged-in user's token
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));  // Ensure shop exists
        // Fetch sales for the shop within the date range
        List<Sale> sales = businessService.getSalesForShop(shopId, startDate, endDate);

        // Generate the PDF report
        ByteArrayInputStream pdfStream = pdfReportServices.generateSalesReport(sales, loggedInUser.getShop(), loggedInUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=sales-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }

    // Generate stock purchase report PDF
    @GetMapping("/stock-purchases")
    public ResponseEntity<?> generateStockPurchaseReport(

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User loggedInUser) throws IOException {
// Fetch the shop details using the shopId from the logged-in user's token
        Long shopId = loggedInUser.getShop().getId(); // Extract the shopId from the logged-in user's token
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));  // Ensure shop exists
        // Fetch stock purchases for the shop within the date range
        List<StockPurchase> stockPurchases = businessService.getStockPurchasesForShop(shopId, startDate, endDate);

        // Generate the PDF report
        ByteArrayInputStream pdfStream = pdfReportServices.generateStockPurchaseReport(stockPurchases, loggedInUser.getShop(), loggedInUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=stock-purchase-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }


}
