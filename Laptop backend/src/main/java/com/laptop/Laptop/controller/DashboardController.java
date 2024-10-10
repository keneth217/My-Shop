package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.DashboardDTO;
import com.laptop.Laptop.dto.ProductCreationRequestDto;
import com.laptop.Laptop.services.DashboardService;
import com.laptop.Laptop.services.PdfReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private PdfReportServices pdfReportServices ;

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
