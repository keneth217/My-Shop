package com.laptop.Laptop.services.Auth;

import com.laptop.Laptop.dto.AuthenticationRequestDto;
import com.laptop.Laptop.dto.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.SignUpRequetDto;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.Roles;
import com.laptop.Laptop.exceptions.CustomerAlreadyExistException;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import com.laptop.Laptop.services.Jwt.UserService;
import com.laptop.Laptop.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements  AuthService{
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final ShopRepository shopRepository;
    private  final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder; // For encoding passwords


    @Override
    public User createUser(SignUpRequetDto signUpRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(signUpRequest.getUserName());
        if (optionalUser.isPresent()) {
            throw new CustomerAlreadyExistException("User already registered with the given username: " + signUpRequest.getUserName());
        }

        // Fetch the shop that the user will belong to
        Shop shop = shopRepository.findById(signUpRequest.getShopId())
                .orElseThrow(() -> new IllegalStateException("Shop not found"));

        User user = User.builder()
                .username(signUpRequest.getUserName())
                .phone(signUpRequest.getPhone())
                .password(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()))
                .role(Roles.CASHIER)  // Default to CASHIER role
                .shop(shop)           // Associate with the shop
                .build();

        User createdUser = userRepository.save(user);
        return createdUser;
    }




    public JWTAuthenticationResponse createAuthToken(@RequestBody AuthenticationRequestDto authenticationRequest) {
        System.out.println("Received authentication request: " + authenticationRequest);

        User user;
        try {
            // Log the shopId being used
            Long shopId = authenticationRequest.getShopId();
            String shopCode= authenticationRequest.getShopCode();
            System.out.println("Using shopId: " + shopId);

            user = userRepository.findByUsernameAndShopId(authenticationRequest.getUserName(), shopId)
                    .orElseThrow(() -> {
                        System.out.println("Error: User not found for username: " + authenticationRequest.getUserName() + " and shopId: " + shopId);
                        return new RuntimeException("User not found or not associated with the specified shop");
                    });
            System.out.println("Success: User found - " + user.getUsername());
        } catch (Exception e) {
            System.out.println("Error fetching user: " + e.getMessage());
            throw new RuntimeException("User not found or not associated with the specified shop", e);
        }

        // Authenticate user
        try {
            // Check if the password matches the stored hashed password
            if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
                System.out.println("Warning: Incorrect password for user: " + authenticationRequest.getUserName());
                throw new RuntimeException("Incorrect login credentials");
            }
            System.out.println("Success: Password matches for user - " + user.getUsername());
        } catch (Exception e) {
            System.out.println("Error during password validation: " + e.getMessage());
            throw new RuntimeException("Incorrect login credentials", e);
        }

        // Load user details and generate JWT with shopId
        UserDetails userDetails;
        try {
            userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getUserName());
            System.out.println("Success: User details loaded for - " + userDetails.getUsername());
        } catch (UsernameNotFoundException e) {
            System.out.println("Error: User not found in userDetailsService: " + e.getMessage());
            throw new RuntimeException("User not found in userDetailsService", e);
        }

        final String jwt = jwtUtils.generateToken(userDetails.getUsername(), authenticationRequest.getShopId(), authenticationRequest.getShopCode());
        System.out.println("Success: Generated JWT token for user - " + userDetails.getUsername());

        // Return JWT response with token, username, role, and shopId
        return new JWTAuthenticationResponse(jwt, user.getUsername(), user.getRole().name(), authenticationRequest.getShopCode(), authenticationRequest.getShopId());
    }








}
