package com.laptop.Laptop.dto;

import lombok.*;

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
}
