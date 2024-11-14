package com.laptop.Laptop.dto;

import com.laptop.Laptop.enums.ShopStatus;
import jakarta.validation.constraints.Future;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShopRegistrationRequestDto {

    private String shopName;
    private String shopUniqueIdentifier;
    private String adminUsername;
    private String adminPassword;
    private String email;
    private LocalDate registrationDate;
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
    private String description;
    private ShopStatus shopStatus;
    private String owner;
    private String shopCode;
    private String phoneNumber;
    private String address;
    private String shopLogoUrl;
    private MultipartFile shopLogo;
}
