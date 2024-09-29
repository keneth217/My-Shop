package com.laptop.Laptop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDto {
    private String userName;
    private String password;
   // private Long shopId;
    private String shopCode;
}
