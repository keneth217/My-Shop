package com.laptop.Laptop.dto;

import lombok.Data;

@Data
public class AuthenticationRequestDto {
    private String userName;
    private String password;
    private Long shopId; //
}
