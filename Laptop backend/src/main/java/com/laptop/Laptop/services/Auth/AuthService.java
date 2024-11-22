package com.laptop.Laptop.services.Auth;




import com.laptop.Laptop.dto.AuthenticationRequestDto;
import com.laptop.Laptop.dto.Responses.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.SignInRequestDto;
import com.laptop.Laptop.dto.SignUpRequestDto;
import com.laptop.Laptop.dto.UserUpdateRequestDto;

import com.laptop.Laptop.entity.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AuthService {
    //User createUser(SignUpRequestDto signUpRequest,String adminUsername);

    User createUser(SignUpRequestDto signUpRequest) ;

   // User updateUserDetails(UserUpdateRequestDto updateRequest);

//    Boolean hasCustomerWithPhone(String phone);
   JWTAuthenticationResponse createAuthToken(@RequestBody AuthenticationRequestDto authenticationRequest);
    public JWTAuthenticationResponse createSuperAuthToken(SignInRequestDto sign);
    UserUpdateRequestDto getLoggedInUserDetails();
    UserUpdateRequestDto updateUserDetails(UserUpdateRequestDto updateRequest);
    List<UserUpdateRequestDto> getUsersForLoggedInUserShop();
    UserUpdateRequestDto updateUserRole(String username, String newRole);
}
