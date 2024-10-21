package com.laptop.Laptop.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JWTAuthenticationResponse {
    private String message;
    private String token;
    private String username;
    private String role;
    private  String shopCode;
    private Long shopId; // Add the shopId field
}
