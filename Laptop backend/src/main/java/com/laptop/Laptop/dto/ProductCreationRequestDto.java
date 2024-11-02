package com.laptop.Laptop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreationRequestDto {

private Long id;
    private String name;
    private double sellingPrice;  // Selling price
    private double buyingPrice;
    private List<String> productFeatures;
    private List<String> productImagesList;
    private List<MultipartFile> productImages;  
    private int stock;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SelectedProductsForReceiptDto {
        private Long saleId;
        private List<Long> selectedProductIds;
    }
}
