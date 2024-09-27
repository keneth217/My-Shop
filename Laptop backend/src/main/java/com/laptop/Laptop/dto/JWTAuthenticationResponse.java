package com.laptop.Laptop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor
public class JWTAuthenticationResponse {
    private String token;
    private String username;
    private String role;


}
