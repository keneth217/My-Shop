package com.laptop.Laptop.services.Auth;




import com.laptop.Laptop.dto.AuthenticationRequestDto;
import com.laptop.Laptop.dto.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.SignUpRequetDto;
import com.laptop.Laptop.entity.User;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    User createUser(SignUpRequetDto signUpRequest);
//    Boolean hasCustomerWithPhone(String phone);
   JWTAuthenticationResponse createAuthToken(@RequestBody AuthenticationRequestDto authenticationRequest);
}
