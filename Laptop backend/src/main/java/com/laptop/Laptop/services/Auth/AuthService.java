package com.laptop.Laptop.services.Auth;




import com.laptop.Laptop.dto.AuthenticationRequestDto;
import com.laptop.Laptop.dto.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.SignUpRequetDto;
import com.laptop.Laptop.dto.UserUpdateRequestDto;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.Roles;
import com.laptop.Laptop.exceptions.CustomerAlreadyExistException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

public interface AuthService {
    //User createUser(SignUpRequetDto signUpRequest,String adminUsername);

    User createUser(SignUpRequetDto signUpRequest) ;

    User updateUserDetails(UserUpdateRequestDto updateRequest);

//    Boolean hasCustomerWithPhone(String phone);
   JWTAuthenticationResponse createAuthToken(@RequestBody AuthenticationRequestDto authenticationRequest);
}
