package com.laptop.Laptop.dto;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

    private String firstName;
    private  String lastName;
    private String userName;
    private String phone;
    private String password;
    private Long shopId;
}
