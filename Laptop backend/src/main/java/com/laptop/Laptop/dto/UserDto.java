package com.laptop.Laptop.dto;


import com.laptop.Laptop.enums.Roles;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Roles role;

}
