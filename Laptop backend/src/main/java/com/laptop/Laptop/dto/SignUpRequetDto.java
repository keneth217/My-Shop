package com.laptop.Laptop.dto;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequetDto {
    private String userName;
    private String phone;
    private String password;
    private Long shopId;

}
