package com.laptop.Laptop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {

    private String firstName;
    private  String lastName;
    private String userName;
    private String currentPassword;
    private String newPassword;
}