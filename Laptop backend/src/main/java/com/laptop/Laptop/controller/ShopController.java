package com.laptop.Laptop.controller;

import com.laptop.Laptop.constants.MyConstants;
import com.laptop.Laptop.dto.Responses.Responsedto;
import com.laptop.Laptop.dto.ShopRegistrationRequestDto;
import com.laptop.Laptop.dto.ShopUpdateRequestDto;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.enums.ShopStatus;
import com.laptop.Laptop.services.PdfReportServices;
import com.laptop.Laptop.services.ShopService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private PdfReportServices pdfReportServices;

    @PostMapping("/register")
    public ResponseEntity<Responsedto> registerShop(@RequestBody ShopRegistrationRequestDto request) {
        Shop newShop = shopService.registerShop(request);
        if (newShop == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Responsedto(MyConstants.ERROR_REGISTER_SHOP_CODE, MyConstants.ERROR_REGISTER_SHOP_MESSAGE));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(
                        MyConstants.REGISTER_SHOP_CODE,
                        MyConstants.REGISTER_SHOP_MESSAGE +
                                "\nYour Unique Shop code is: " +"\n" + newShop.getShopCode()));
    }
        // Activate a shop
    @PostMapping("/{shopId}/activate")
    public ResponseEntity<Shop> activateShop(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopRegistrationRequestDto request) {
        Shop activatedShop = shopService.activateShop(shopId, request);
        return ResponseEntity.ok(activatedShop);
    }

    // Deactivate a shop
    @PutMapping("/{shopId}/deactivate")
    public ResponseEntity<Shop> deactivateShop(@PathVariable Long shopId) {
        Shop deactivatedShop = shopService.deactiveShop(shopId);
        return ResponseEntity.ok(deactivatedShop);
    }


    // Get all registered shops
    @GetMapping
    public ResponseEntity<List<ShopUpdateRequestDto>> getAllShops() {
        List<ShopUpdateRequestDto> shops = shopService.getAllShops();
        return ResponseEntity.ok(shops);
    }

    // Get all shops by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Shop>> getAllShopsByStatus(@PathVariable ShopStatus status) {
        List<Shop> shops = shopService.getAllShopsByStatus(status);
        return ResponseEntity.ok(shops);
    }

    // Generate shop list report PDF
    @GetMapping("/pdf/report")
    public ResponseEntity<byte[]> generateShopListReport() throws IOException {

        // Fetch all shops
        List<ShopUpdateRequestDto> shops = shopService.getAllShops();

        // Generate the PDF report
        ByteArrayInputStream pdfStream = pdfReportServices.generateShopListReport(shops);

        // Read the PDF bytes from the InputStream
        byte[] pdfBytes = pdfStream.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=shop-list-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes); // Return the byte array representing the PDF
    }


    @PutMapping("/update/{shopId}")
    public ResponseEntity<ShopUpdateRequestDto> updateShopDetails(
            @PathVariable Long shopId,
            @ModelAttribute ShopUpdateRequestDto request) {

        ShopUpdateRequestDto updatedShop = shopService.updateShopDetails(shopId, request);

        return ResponseEntity.ok(updatedShop);
    }
}
