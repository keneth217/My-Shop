package com.laptop.Laptop.dto;

import com.laptop.Laptop.enums.ShopStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopUpdateRequestDto {
private Long id;
    private String shopName;
    private String address;
    private String owner;
    private String shopCode;
    private String phoneNumber;
    private String description;
    private MultipartFile shopLogo;  // Logo file for upload
    private String shopLogoUrl;
    private ShopStatus status;
    private LocalDate expiryDate;
    private LocalDate registrationDate;

}
