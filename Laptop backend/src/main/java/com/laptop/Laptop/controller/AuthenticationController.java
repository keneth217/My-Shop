package com.laptop.Laptop.controller;
import com.laptop.Laptop.configs.JwtAuthenticationFilter;
import com.laptop.Laptop.constants.AuthConstants;
import com.laptop.Laptop.dto.*;
import com.laptop.Laptop.dto.Responses.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.Responses.Responsedto;
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

import java.util.Collections;
import java.util.List;


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

    @GetMapping("/shop-users")
    public ResponseEntity<List<UserUpdateRequestDto>> getUsersForLoggedInUserShop() {
        try {
            List<UserUpdateRequestDto> users = authService.getUsersForLoggedInUserShop();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }
    @PostMapping("/create-user")
    public ResponseEntity<Responsedto> createUser(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        // The admin creating the user is already authenticated, so no need to pass admin info
        authService.createUser(signUpRequest);

        String userName = signUpRequest.getUserName();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Responsedto(AuthConstants.ACCOUNT_CREATION_CODE,
                        userName + " ," + AuthConstants.ACCOUNT_CREATION));
    }


    // Login endpoint to authenticate the user and return a JWT
    @PostMapping("/login")
    public ResponseEntity<JWTAuthenticationResponse> login(@RequestBody AuthenticationRequestDto authenticationRequest) {
        return ResponseEntity.ok(authService.createAuthToken(authenticationRequest));
    }


    @PostMapping("/super/login")
    public ResponseEntity<JWTAuthenticationResponse> loginSuper(@RequestBody SignInRequestDto login) {
        return ResponseEntity.ok(authService.createSuperAuthToken(login));
    }

    // Endpoint for getting the details of the currently logged-in user
    @GetMapping("/me")
    public ResponseEntity<UserUpdateRequestDto> getLoggedInUser() {
        UserUpdateRequestDto user = authService.getLoggedInUserDetails();
        return ResponseEntity.ok(user);
    }

    // Endpoint for updating user details
    @PutMapping("/me")
    public ResponseEntity<UserUpdateRequestDto> updateUser(@ModelAttribute  UserUpdateRequestDto updateRequest) {
        UserUpdateRequestDto updatedUser = authService.updateUserDetails(updateRequest);
        return ResponseEntity.ok(updatedUser);
    }
    @PutMapping("/{username}/role")
    public ResponseEntity<UserUpdateRequestDto> updateUserRole(
            @PathVariable String username,
            @RequestBody UserRoleUpdateDto userRoleUpdateDto) {

        // Update the user's role
        UserUpdateRequestDto updatedUser = authService.updateUserRole(username, userRoleUpdateDto.getRole());

        // Return the updated user details
        return ResponseEntity.ok(updatedUser);
    }

    
}
