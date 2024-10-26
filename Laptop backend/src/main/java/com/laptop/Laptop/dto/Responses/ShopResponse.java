package com.laptop.Laptop.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopResponse {
    private String shopName;
    private String shopAddress;
    private String shopCode;
    private String shopPhone;
    private String shopLogo;
}
