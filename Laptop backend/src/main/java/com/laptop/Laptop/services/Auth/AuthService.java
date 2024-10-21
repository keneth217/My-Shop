package com.laptop.Laptop.services.Auth;




import com.laptop.Laptop.dto.AuthenticationRequestDto;
import com.laptop.Laptop.dto.Responses.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.SignUpRequetDto;
import com.laptop.Laptop.dto.UserUpdateRequestDto;
import com.laptop.Laptop.entity.User;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    //User createUser(SignUpRequetDto signUpRequest,String adminUsername);

    User createUser(SignUpRequetDto signUpRequest) ;

    User updateUserDetails(UserUpdateRequestDto updateRequest);

//    Boolean hasCustomerWithPhone(String phone);
   JWTAuthenticationResponse createAuthToken(@RequestBody AuthenticationRequestDto authenticationRequest);
}
