package com.laptop.Laptop.controller;
import com.laptop.Laptop.configs.JwtAuthenticationFilter;
import com.laptop.Laptop.constants.AuthConstants;
import com.laptop.Laptop.dto.AuthenticationRequestDto;
import com.laptop.Laptop.dto.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.Responsedto;
import com.laptop.Laptop.dto.SignUpRequetDto;
import com.laptop.Laptop.repository.UserRepository;
import com.laptop.Laptop.services.Auth.AuthService;
import com.laptop.Laptop.services.Jwt.UserService;
import com.laptop.Laptop.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/sign")
    public ResponseEntity<Responsedto> createUser(@Valid @RequestBody SignUpRequetDto signUpRequest) {
        authService.createUser(signUpRequest);
        String userName = signUpRequest.getUserName();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(AuthConstants.ACCOUNT_CREATION_CODE, userName + " " + AuthConstants.ACCOUNT_CREATION));
    }

    // Login endpoint to authenticate the user and return a JWT
    @PostMapping("/login")
    public ResponseEntity<JWTAuthenticationResponse> login(@RequestBody AuthenticationRequestDto authenticationRequest) {
        return ResponseEntity.ok(authService.createAuthToken(authenticationRequest));
    }
}
