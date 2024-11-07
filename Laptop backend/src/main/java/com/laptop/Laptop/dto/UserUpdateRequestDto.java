package com.laptop.Laptop.dto;

import com.laptop.Laptop.enums.Roles;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {

    private String firstName;
    private  String lastName;
    private String userName;
    private String shopCode;
    private String phone;
    private String currentPassword;
    private String newPassword;
    private Roles role;
    private MultipartFile userImage;  // Logo file for upload
    private String UserImageUrl;


}
