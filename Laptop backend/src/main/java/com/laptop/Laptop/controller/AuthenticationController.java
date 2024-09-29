package com.laptop.Laptop.controller;
import com.laptop.Laptop.configs.JwtAuthenticationFilter;
import com.laptop.Laptop.constants.AuthConstants;
import com.laptop.Laptop.dto.*;
import com.laptop.Laptop.entity.User;
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

    @PostMapping("/create-user")
    public ResponseEntity<Responsedto> createUser(@Valid @RequestBody SignUpRequetDto signUpRequest) {
        // The admin creating the user is already authenticated, so no need to pass admin info
        authService.createUser(signUpRequest);

        String userName = signUpRequest.getUserName();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(AuthConstants.ACCOUNT_CREATION_CODE,
                        userName + " " + AuthConstants.ACCOUNT_CREATION));
    }
    @PutMapping("/update-profile")
    public ResponseEntity<User> updateUserDetails(@Valid @RequestBody UserUpdateRequestDto updateRequest) {
        User updatedUser = authService.updateUserDetails(updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    // Login endpoint to authenticate the user and return a JWT
    @PostMapping("/login")
    public ResponseEntity<JWTAuthenticationResponse> login(@RequestBody AuthenticationRequestDto authenticationRequest) {
        return ResponseEntity.ok(authService.createAuthToken(authenticationRequest));
    }
}
