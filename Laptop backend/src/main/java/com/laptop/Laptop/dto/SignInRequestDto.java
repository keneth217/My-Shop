package com.laptop.Laptop.dto;

import lombok.*;

@Builder
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDto {
    private String userName;
    private String password;

}
