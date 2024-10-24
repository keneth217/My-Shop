package com.laptop.Laptop.dto;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {

    private String firstName;
    private  String lastName;
    private String userName;
    private String currentPassword;
    private String newPassword;
}
